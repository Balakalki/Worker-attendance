package com.example.demo.workforce;

import org.hibernate.annotations.Check;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

@Entity
@Table(
        name = "workers",
        indexes = {
                @Index(name = "idx_workers_designation", columnList = "designation"),
                @Index(name = "idx_workers_active", columnList = "active")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_workers_phone", columnNames = "phone")
        }
)
@Check(constraints = "daily_wage_rate >= 0")
public class Worker {
    @Id
    @SequenceGenerator(
            name = "worker_sequence",
            sequenceName = "worker_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "worker_sequence",
            strategy = GenerationType.SEQUENCE
    )
    private Long id;

    @NotBlank
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @NotBlank
    @Column(name = "phone", nullable = false, length = 20)
    private String phone;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "designation", nullable = false, length = 30)
    private WorkerDesignation designation;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "daily_wage_rate", nullable = false, precision = 10, scale = 2)
    private BigDecimal dailyWageRate;

    @Column(name = "active", nullable = false)
    private Boolean active = true;

    public Worker() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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
