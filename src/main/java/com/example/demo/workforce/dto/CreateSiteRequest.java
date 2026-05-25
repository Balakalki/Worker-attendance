package com.example.demo.workforce.dto;

import javax.validation.constraints.NotBlank;

public class CreateSiteRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String location;

    private Boolean active = true;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
