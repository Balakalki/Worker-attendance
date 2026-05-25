package com.example.demo.workforce.dto;

import java.math.BigDecimal;
import java.time.Instant;

public class AttendanceLogResponse {
    private Long id;
    private Long workerId;
    private String workerName;
    private Long siteId;
    private String siteName;
    private Instant clockInAt;
    private Instant clockOutAt;
    private BigDecimal totalHoursWorked;
    private BigDecimal overtimeHours;
    private Boolean flagged;

    public AttendanceLogResponse(Long id, Long workerId, String workerName, Long siteId, String siteName,
                                 Instant clockInAt, Instant clockOutAt, BigDecimal totalHoursWorked,
                                 BigDecimal overtimeHours, Boolean flagged) {
        this.id = id;
        this.workerId = workerId;
        this.workerName = workerName;
        this.siteId = siteId;
        this.siteName = siteName;
        this.clockInAt = clockInAt;
        this.clockOutAt = clockOutAt;
        this.totalHoursWorked = totalHoursWorked;
        this.overtimeHours = overtimeHours;
        this.flagged = flagged;
    }

    public Long getId() {
        return id;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public Long getSiteId() {
        return siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public Instant getClockInAt() {
        return clockInAt;
    }

    public Instant getClockOutAt() {
        return clockOutAt;
    }

    public BigDecimal getTotalHoursWorked() {
        return totalHoursWorked;
    }

    public BigDecimal getOvertimeHours() {
        return overtimeHours;
    }

    public Boolean getFlagged() {
        return flagged;
    }
}
