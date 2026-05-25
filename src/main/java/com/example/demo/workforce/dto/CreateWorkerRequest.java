package com.example.demo.workforce.dto;

import com.example.demo.workforce.WorkerDesignation;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class CreateWorkerRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String phone;

    @NotNull
    private WorkerDesignation designation;

    @NotNull
    @DecimalMin("0.0")
    private BigDecimal dailyWageRate;

    private Boolean active = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
