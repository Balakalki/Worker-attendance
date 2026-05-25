package com.example.demo.workforce.event;

import java.math.BigDecimal;
import java.time.YearMonth;

public class OvertimeSettledEvent {
    private final Long workerId;
    private final String workerName;
    private final String phone;
    private final YearMonth month;
    private final BigDecimal totalAmount;

    public OvertimeSettledEvent(Long workerId, String workerName, String phone, YearMonth month, BigDecimal totalAmount) {
        this.workerId = workerId;
        this.workerName = workerName;
        this.phone = phone;
        this.month = month;
        this.totalAmount = totalAmount;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public String getPhone() {
        return phone;
    }

    public YearMonth getMonth() {
        return month;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }
}
