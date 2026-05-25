package com.example.demo.workforce.dto;

import com.example.demo.workforce.SettlementStatus;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.List;

public class OvertimeSummaryResponse {
    private Long workerId;
    private String workerName;
    private YearMonth month;
    private BigDecimal totalOvertimeHours;
    private BigDecimal totalPayoutAmount;
    private SettlementStatus settlementStatus;
    private List<OvertimeBreakdownResponse> breakdown;

    public OvertimeSummaryResponse(Long workerId, String workerName, YearMonth month, BigDecimal totalOvertimeHours,
                                   BigDecimal totalPayoutAmount, SettlementStatus settlementStatus,
                                   List<OvertimeBreakdownResponse> breakdown) {
        this.workerId = workerId;
        this.workerName = workerName;
        this.month = month;
        this.totalOvertimeHours = totalOvertimeHours;
        this.totalPayoutAmount = totalPayoutAmount;
        this.settlementStatus = settlementStatus;
        this.breakdown = breakdown;
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

    public BigDecimal getTotalOvertimeHours() {
        return totalOvertimeHours;
    }

    public BigDecimal getTotalPayoutAmount() {
        return totalPayoutAmount;
    }

    public SettlementStatus getSettlementStatus() {
        return settlementStatus;
    }

    public List<OvertimeBreakdownResponse> getBreakdown() {
        return breakdown;
    }
}
