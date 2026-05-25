package com.example.demo.workforce;

import com.example.demo.workforce.dto.ActiveAttendanceResponse;
import com.example.demo.workforce.dto.AttendanceLogResponse;
import com.example.demo.workforce.dto.ClockInRequest;
import com.example.demo.workforce.dto.ClockOutRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {
    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @PostMapping("/clock-in")
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceLogResponse clockIn(@Valid @RequestBody ClockInRequest request) {
        return attendanceService.clockIn(request.getWorkerId(), request.getSiteId());
    }

    @PostMapping("/clock-out")
    public AttendanceLogResponse clockOut(@Valid @RequestBody ClockOutRequest request) {
        return attendanceService.clockOut(request.getWorkerId());
    }

    @GetMapping("/active")
    public List<ActiveAttendanceResponse> findActiveWorkers() {
        return attendanceService.findActiveWorkers();
    }

    @GetMapping("/log")
    public Page<AttendanceLogResponse> findWorkerAttendanceLog(
            @RequestParam Long workerId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to,
            Pageable pageable) {
        return attendanceService.findWorkerAttendanceLog(workerId, from, to, pageable);
    }
}
