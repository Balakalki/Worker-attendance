package com.example.demo.workforce;

import com.example.demo.workforce.dto.ActiveAttendanceResponse;
import com.example.demo.workforce.dto.AttendanceLogResponse;
import com.example.demo.workforce.exception.WorkforceApiException;
import com.example.demo.workforce.redis.ActiveAttendanceCacheService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.List;

@Service
public class AttendanceService {
    private static final BigDecimal STANDARD_SHIFT_HOURS = BigDecimal.valueOf(8);
    private static final BigDecimal MAX_SHIFT_HOURS = BigDecimal.valueOf(16);
    private static final BigDecimal MONTHLY_OVERTIME_CAP_HOURS = BigDecimal.valueOf(60);
    private static final BigDecimal FIRST_OVERTIME_TIER_HOURS = BigDecimal.valueOf(2);
    private static final BigDecimal FIRST_OVERTIME_MULTIPLIER = BigDecimal.valueOf(1.5);
    private static final BigDecimal SECOND_OVERTIME_MULTIPLIER = BigDecimal.valueOf(2);
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Kolkata");

    private final WorkerRepository workerRepository;
    private final SiteRepository siteRepository;
    private final AttendanceRepository attendanceRepository;
    private final OvertimeRepository overtimeRepository;
    private final ActiveAttendanceCacheService activeAttendanceCacheService;

    public AttendanceService(WorkerRepository workerRepository,
                             SiteRepository siteRepository,
                             AttendanceRepository attendanceRepository,
                             OvertimeRepository overtimeRepository,
                             ActiveAttendanceCacheService activeAttendanceCacheService) {
        this.workerRepository = workerRepository;
        this.siteRepository = siteRepository;
        this.attendanceRepository = attendanceRepository;
        this.overtimeRepository = overtimeRepository;
        this.activeAttendanceCacheService = activeAttendanceCacheService;
    }

    @Transactional
    public AttendanceLogResponse clockIn(Long workerId, Long siteId) {
        Worker worker = workerRepository.findById(workerId)
                .orElseThrow(() -> new WorkforceApiException("WORKER_NOT_FOUND", "Worker not found", HttpStatus.NOT_FOUND));
        if (!Boolean.TRUE.equals(worker.getActive())) {
            throw new WorkforceApiException("WORKER_INACTIVE", "Worker is inactive", HttpStatus.BAD_REQUEST);
        }

        Site site = siteRepository.findById(siteId)
                .orElseThrow(() -> new WorkforceApiException("SITE_NOT_FOUND", "Site not found", HttpStatus.NOT_FOUND));
        if (!Boolean.TRUE.equals(site.getActive())) {
            throw new WorkforceApiException("SITE_INACTIVE", "Site is inactive", HttpStatus.BAD_REQUEST);
        }

        attendanceRepository.findByWorkerIdAndClockOutAtIsNull(workerId)
                .ifPresent(activeAttendance -> {
                    throw new WorkforceApiException(
                            "DUPLICATE_CLOCK_IN",
                            "Worker is already clocked in at Site: " + activeAttendance.getSite().getName(),
                            HttpStatus.CONFLICT
                    );
                });

        Instant clockInAt = Instant.now();
        AttendanceLog attendanceLog = new AttendanceLog();
        attendanceLog.setWorker(worker);
        attendanceLog.setSite(site);
        attendanceLog.setClockInAt(clockInAt);

        AttendanceLog savedAttendanceLog = attendanceRepository.save(attendanceLog);
        activeAttendanceCacheService.addActiveWorker(worker, site, clockInAt);
        return toResponse(savedAttendanceLog);
    }

    @Transactional
    public AttendanceLogResponse clockOut(Long workerId) {
        AttendanceLog attendanceLog = attendanceRepository.findByWorkerIdAndClockOutAtIsNull(workerId)
                .orElseThrow(() -> new WorkforceApiException("CLOCK_IN_NOT_FOUND", "Worker is not currently clocked in", HttpStatus.BAD_REQUEST));

        Instant clockOutAt = Instant.now();
        BigDecimal totalHours = calculateHours(attendanceLog.getClockInAt(), clockOutAt);
        BigDecimal uncappedOvertimeHours = totalHours.subtract(STANDARD_SHIFT_HOURS).max(BigDecimal.ZERO);
        BigDecimal cappedOvertimeHours = capOvertimeForMonth(workerId, clockOutAt, uncappedOvertimeHours);

        attendanceLog.setClockOutAt(clockOutAt);
        attendanceLog.setTotalHoursWorked(totalHours);
        attendanceLog.setOvertimeHours(uncappedOvertimeHours);
        attendanceLog.setFlagged(totalHours.compareTo(MAX_SHIFT_HOURS) > 0);

        AttendanceLog savedAttendanceLog = attendanceRepository.save(attendanceLog);
        if (cappedOvertimeHours.compareTo(BigDecimal.ZERO) > 0) {
            overtimeRepository.save(createOvertimeEntry(savedAttendanceLog, cappedOvertimeHours, clockOutAt));
        }

        activeAttendanceCacheService.removeActiveWorker(workerId);
        return toResponse(savedAttendanceLog);
    }

    @Transactional(readOnly = true)
    public List<ActiveAttendanceResponse> findActiveWorkers() {
        return activeAttendanceCacheService.findAllActiveWorkers();
    }

    @Transactional(readOnly = true)
    public Page<AttendanceLogResponse> findWorkerAttendanceLog(Long workerId, LocalDate from, LocalDate to, Pageable pageable) {
        if (from.isAfter(to)) {
            throw new WorkforceApiException("INVALID_DATE_RANGE", "from date must be before or equal to to date", HttpStatus.BAD_REQUEST);
        }
        if (!workerRepository.existsById(workerId)) {
            throw new WorkforceApiException("WORKER_NOT_FOUND", "Worker not found", HttpStatus.NOT_FOUND);
        }

        Instant fromInstant = from.atStartOfDay(BUSINESS_ZONE).toInstant();
        Instant toInstant = to.atTime(LocalTime.MAX).atZone(BUSINESS_ZONE).toInstant();
        return attendanceRepository.findByWorkerIdAndClockInAtBetween(workerId, fromInstant, toInstant, pageable)
                .map(this::toResponse);
    }

    private OvertimeEntry createOvertimeEntry(AttendanceLog attendanceLog, BigDecimal overtimeHours, Instant clockOutAt) {
        BigDecimal amount = calculateOvertimeAmount(attendanceLog.getWorker().getDailyWageRate(), overtimeHours);
        BigDecimal averageRate = amount.divide(overtimeHours, 2, RoundingMode.HALF_UP);

        OvertimeEntry overtimeEntry = new OvertimeEntry();
        overtimeEntry.setWorker(attendanceLog.getWorker());
        overtimeEntry.setAttendanceLog(attendanceLog);
        overtimeEntry.setDate(LocalDate.ofInstant(clockOutAt, BUSINESS_ZONE));
        overtimeEntry.setOvertimeHours(overtimeHours);
        overtimeEntry.setOvertimeRateApplied(averageRate);
        overtimeEntry.setAmount(amount);
        overtimeEntry.setSettlementStatus(SettlementStatus.PENDING);
        return overtimeEntry;
    }

    private BigDecimal capOvertimeForMonth(Long workerId, Instant clockOutAt, BigDecimal overtimeHours) {
        if (overtimeHours.compareTo(BigDecimal.ZERO) <= 0) {
            return BigDecimal.ZERO;
        }

        LocalDate clockOutDate = LocalDate.ofInstant(clockOutAt, BUSINESS_ZONE);
        LocalDate monthStart = clockOutDate.withDayOfMonth(1);
        LocalDate monthEnd = clockOutDate.withDayOfMonth(clockOutDate.lengthOfMonth());
        BigDecimal usedHours = overtimeRepository.sumOvertimeHoursForWorkerBetween(workerId, monthStart, monthEnd);
        BigDecimal remainingHours = MONTHLY_OVERTIME_CAP_HOURS.subtract(usedHours).max(BigDecimal.ZERO);
        return overtimeHours.min(remainingHours);
    }

    private BigDecimal calculateHours(Instant clockInAt, Instant clockOutAt) {
        long minutes = Duration.between(clockInAt, clockOutAt).toMinutes();
        return BigDecimal.valueOf(minutes)
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);
    }

    private BigDecimal calculateOvertimeAmount(BigDecimal dailyWageRate, BigDecimal overtimeHours) {
        BigDecimal hourlyRate = dailyWageRate.divide(STANDARD_SHIFT_HOURS, 2, RoundingMode.HALF_UP);
        BigDecimal firstTierHours = overtimeHours.min(FIRST_OVERTIME_TIER_HOURS);
        BigDecimal secondTierHours = overtimeHours.subtract(FIRST_OVERTIME_TIER_HOURS).max(BigDecimal.ZERO);

        BigDecimal firstTierAmount = firstTierHours.multiply(hourlyRate).multiply(FIRST_OVERTIME_MULTIPLIER);
        BigDecimal secondTierAmount = secondTierHours.multiply(hourlyRate).multiply(SECOND_OVERTIME_MULTIPLIER);
        return firstTierAmount.add(secondTierAmount).setScale(2, RoundingMode.HALF_UP);
    }

    private AttendanceLogResponse toResponse(AttendanceLog attendanceLog) {
        return new AttendanceLogResponse(
                attendanceLog.getId(),
                attendanceLog.getWorker().getId(),
                attendanceLog.getWorker().getName(),
                attendanceLog.getSite().getId(),
                attendanceLog.getSite().getName(),
                attendanceLog.getClockInAt(),
                attendanceLog.getClockOutAt(),
                attendanceLog.getTotalHoursWorked(),
                attendanceLog.getOvertimeHours(),
                attendanceLog.getFlagged()
        );
    }
}
