package com.example.demo.workforce;

import com.example.demo.workforce.dto.OvertimeBreakdownResponse;
import com.example.demo.workforce.dto.OvertimeSettlementResponse;
import com.example.demo.workforce.dto.OvertimeSummaryResponse;
import com.example.demo.workforce.exception.WorkforceApiException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.ZoneId;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class OvertimeService {
    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Kolkata");

    private final WorkerRepository workerRepository;
    private final OvertimeRepository overtimeRepository;

    public OvertimeService(WorkerRepository workerRepository, OvertimeRepository overtimeRepository) {
        this.workerRepository = workerRepository;
        this.overtimeRepository = overtimeRepository;
    }

    @Transactional(readOnly = true)
    public OvertimeSummaryResponse getMonthlySummary(Long workerId, String monthValue) {
        Worker worker = findWorker(workerId);
        YearMonth month = parseMonth(monthValue);
        List<OvertimeEntry> entries = findMonthlyEntries(workerId, month);
        return buildSummary(worker, month, entries);
    }

    @Transactional
    public OvertimeSettlementResponse settleMonthlyOvertime(Long workerId, String monthValue) {
        Worker worker = findWorker(workerId);
        YearMonth month = parseMonth(monthValue);
        YearMonth currentMonth = YearMonth.now(BUSINESS_ZONE);
        if (!month.isBefore(currentMonth)) {
            throw new WorkforceApiException("CURRENT_MONTH_SETTLEMENT_NOT_ALLOWED", "Cannot settle current or future month overtime", HttpStatus.BAD_REQUEST);
        }

        List<OvertimeEntry> entries = findMonthlyEntries(workerId, month);
        if (entries.isEmpty()) {
            throw new WorkforceApiException("OVERTIME_NOT_FOUND", "No overtime entries found for this worker and month", HttpStatus.NOT_FOUND);
        }

        boolean allSettled = entries.stream()
                .allMatch(entry -> entry.getSettlementStatus() == SettlementStatus.SETTLED);
        if (allSettled) {
            throw new WorkforceApiException("OVERTIME_ALREADY_SETTLED", "Overtime entries are already settled for this worker and month", HttpStatus.CONFLICT);
        }

        BigDecimal totalAmount = entries.stream()
                .map(OvertimeEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        entries.stream()
                .filter(entry -> entry.getSettlementStatus() == SettlementStatus.PENDING)
                .forEach(entry -> entry.setSettlementStatus(SettlementStatus.SETTLED));

        overtimeRepository.saveAll(entries);
        return new OvertimeSettlementResponse(worker.getId(), worker.getName(), month, totalAmount, entries.size());
    }

    private Worker findWorker(Long workerId) {
        return workerRepository.findById(workerId)
                .orElseThrow(() -> new WorkforceApiException("WORKER_NOT_FOUND", "Worker not found", HttpStatus.NOT_FOUND));
    }

    private YearMonth parseMonth(String monthValue) {
        try {
            return YearMonth.parse(monthValue);
        } catch (DateTimeParseException exception) {
            throw new WorkforceApiException("INVALID_MONTH", "month must use YYYY-MM format", HttpStatus.BAD_REQUEST);
        }
    }

    private List<OvertimeEntry> findMonthlyEntries(Long workerId, YearMonth month) {
        LocalDate from = month.atDay(1);
        LocalDate to = month.atEndOfMonth();
        return overtimeRepository.findByWorkerIdAndDateBetweenOrderByDateAsc(workerId, from, to);
    }

    private OvertimeSummaryResponse buildSummary(Worker worker, YearMonth month, List<OvertimeEntry> entries) {
        BigDecimal totalHours = entries.stream()
                .map(OvertimeEntry::getOvertimeHours)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        BigDecimal totalAmount = entries.stream()
                .map(OvertimeEntry::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        SettlementStatus settlementStatus = entries.isEmpty() || entries.stream()
                .anyMatch(entry -> entry.getSettlementStatus() == SettlementStatus.PENDING)
                ? SettlementStatus.PENDING
                : SettlementStatus.SETTLED;

        List<OvertimeBreakdownResponse> breakdown = entries.stream()
                .map(entry -> new OvertimeBreakdownResponse(
                        entry.getDate(),
                        entry.getOvertimeHours(),
                        entry.getOvertimeRateApplied(),
                        entry.getAmount(),
                        entry.getSettlementStatus()
                ))
                .collect(Collectors.toList());

        return new OvertimeSummaryResponse(
                worker.getId(),
                worker.getName(),
                month,
                totalHours,
                totalAmount,
                settlementStatus,
                breakdown
        );
    }
}
