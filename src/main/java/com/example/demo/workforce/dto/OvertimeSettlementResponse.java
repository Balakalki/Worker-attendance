package com.example.demo.workforce.dto;

import java.math.BigDecimal;
import java.time.YearMonth;

public class OvertimeSettlementResponse {
    private Long workerId;
    private String workerName;
    private YearMonth month;
    private BigDecimal totalAmount;
    private int settledEntries;

    public OvertimeSettlementResponse(Long workerId, String workerName, YearMonth month, BigDecimal totalAmount,
                                      int settledEntries) {
        this.workerId = workerId;
        this.workerName = workerName;
        this.month = month;
        this.totalAmount = totalAmount;
        this.settledEntries = settledEntries;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public String getWorkerName() {
        return workerName;
    }

    public YearMonth getMonth() {
        return month;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public int getSettledEntries() {
        return settledEntries;
    }
}
