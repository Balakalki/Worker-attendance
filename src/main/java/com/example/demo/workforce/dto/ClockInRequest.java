package com.example.demo.workforce.dto;

import javax.validation.constraints.NotNull;

public class ClockInRequest {
    @NotNull
    private Long workerId;

    @NotNull
    private Long siteId;

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public Long getSiteId() {
        return siteId;
    }

    public void setSiteId(Long siteId) {
        this.siteId = siteId;
    }
}
