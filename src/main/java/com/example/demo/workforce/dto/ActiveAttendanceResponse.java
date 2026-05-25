package com.example.demo.workforce.dto;

import com.example.demo.workforce.WorkerDesignation;

import java.math.BigDecimal;
import java.time.Instant;

public class ActiveAttendanceResponse {
    private Long workerId;
    private String workerName;
    private String phone;
    private WorkerDesignation designation;
    private BigDecimal dailyWageRate;
    private Long siteId;
    private String siteName;
    private String siteLocation;
    private Instant clockInAt;

    public ActiveAttendanceResponse() {
    }

    public ActiveAttendanceResponse(Long workerId, String workerName, String phone, WorkerDesignation designation,
                                    BigDecimal dailyWageRate, Long siteId, String siteName, String siteLocation,
                                    Instant clockInAt) {
        this.workerId = workerId;
        this.workerName = workerName;
        this.phone = phone;
        this.designation = designation;
        this.dailyWageRate = dailyWageRate;
        this.siteId = siteId;
        this.siteName = siteName;
        this.siteLocation = siteLocation;
        this.clockInAt = clockInAt;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public WorkerDesignation getDesignation() {
        return designation;
    }

    public void setDesignation(WorkerDesignation designation) {
        this.designation = designation;
    }

    public BigDecimal getDailyWageRate() {
        return dailyWageRate;
    }

    public void setDailyWageRate(BigDecimal dailyWageRate) {
        this.dailyWageRate = dailyWageRate;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }

    public String getSiteName() {
        return siteName;
    }

    public void setSiteName(String siteName) {
        this.siteName = siteName;
    }

    public String getSiteLocation() {
        return siteLocation;
    }

    public void setSiteLocation(String siteLocation) {
        this.siteLocation = siteLocation;
    }

    public Instant getClockInAt() {
        return clockInAt;
    }

    public void setClockInAt(Instant clockInAt) {
        this.clockInAt = clockInAt;
    }
}
