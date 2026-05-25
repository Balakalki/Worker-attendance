package com.example.demo.workforce;

import org.hibernate.annotations.Check;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(
        name = "attendance_logs",
        indexes = {
                @Index(name = "idx_attendance_worker_clock_in", columnList = "worker_id, clock_in_at"),
                @Index(name = "idx_attendance_site_clock_in", columnList = "site_id, clock_in_at"),
                @Index(name = "idx_attendance_open_worker", columnList = "worker_id, clock_out_at"),
                @Index(name = "idx_attendance_flagged", columnList = "flagged")
        }
)
@Check(constraints = "(clock_out_at IS NULL OR clock_out_at >= clock_in_at) AND (total_hours_worked IS NULL OR total_hours_worked >= 0) AND (overtime_hours IS NULL OR overtime_hours >= 0)")
public class AttendanceLog {
    @Id
    @SequenceGenerator(
            name = "attendance_log_sequence",
            sequenceName = "attendance_log_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "attendance_log_sequence",
            strategy = GenerationType.SEQUENCE
    )
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "worker_id", nullable = false)
    private Worker worker;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "site_id", nullable = false)
    private Site site;

    @NotNull
    @Column(name = "clock_in_at", nullable = false)
    private Instant clockInAt;

    @Column(name = "clock_out_at")
    private Instant clockOutAt;

    @DecimalMin(value = "0.0")
    @Column(name = "total_hours_worked", precision = 6, scale = 2)
    private BigDecimal totalHoursWorked;

    @DecimalMin(value = "0.0")
    @Column(name = "overtime_hours", precision = 6, scale = 2)
    private BigDecimal overtimeHours;

    @Column(name = "flagged", nullable = false)
    private Boolean flagged = false;

    public AttendanceLog() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Worker getWorker() {
        return worker;
    }

    public void setWorker(Worker worker) {
        this.worker = worker;
    }

    public Site getSite() {
        return site;
    }

    public void setSite(Site site) {
        this.site = site;
    }

    public Instant getClockInAt() {
        return clockInAt;
    }

    public void setClockInAt(Instant clockInAt) {
        this.clockInAt = clockInAt;
    }

    public Instant getClockOutAt() {
        return clockOutAt;
    }

    public void setClockOutAt(Instant clockOutAt) {
        this.clockOutAt = clockOutAt;
    }

    public BigDecimal getTotalHoursWorked() {
        return totalHoursWorked;
    }

    public void setTotalHoursWorked(BigDecimal totalHoursWorked) {
        this.totalHoursWorked = totalHoursWorked;
    }

    public BigDecimal getOvertimeHours() {
        return overtimeHours;
    }

    public void setOvertimeHours(BigDecimal overtimeHours) {
        this.overtimeHours = overtimeHours;
    }

    public Boolean getFlagged() {
        return flagged;
    }

    public void setFlagged(Boolean flagged) {
        this.flagged = flagged;
    }
}
