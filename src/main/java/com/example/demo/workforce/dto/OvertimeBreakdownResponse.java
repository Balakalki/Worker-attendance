package com.example.demo.workforce.dto;

import com.example.demo.workforce.SettlementStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OvertimeBreakdownResponse {
    private LocalDate date;
    private BigDecimal overtimeHours;
    private BigDecimal overtimeRateApplied;
    private BigDecimal amount;
    private SettlementStatus settlementStatus;

    public OvertimeBreakdownResponse(LocalDate date, BigDecimal overtimeHours, BigDecimal overtimeRateApplied,
                                     BigDecimal amount, SettlementStatus settlementStatus) {
        this.date = date;
        this.overtimeHours = overtimeHours;
        this.overtimeRateApplied = overtimeRateApplied;
        this.amount = amount;
        this.settlementStatus = settlementStatus;
    }

    public LocalDate getDate() {
        return date;
    }

    public BigDecimal getOvertimeHours() {
        return overtimeHours;
    }

    public BigDecimal getOvertimeRateApplied() {
        return overtimeRateApplied;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public SettlementStatus getSettlementStatus() {
        return settlementStatus;
    }
}
