package com.example.demo.workforce.dto;

import javax.validation.constraints.NotNull;

public class ClockOutRequest {
    @NotNull
    private Long workerId;

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }
}
