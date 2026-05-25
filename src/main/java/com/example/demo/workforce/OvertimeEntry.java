package com.example.demo.workforce;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.Check;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "overtime_entries",
        indexes = {
                @Index(name = "idx_overtime_worker_date", columnList = "worker_id, overtime_date"),
                @Index(name = "idx_overtime_worker_status", columnList = "worker_id, settlement_status"),
                @Index(name = "idx_overtime_settlement_status", columnList = "settlement_status")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_overtime_attendance", columnNames = "attendance_log_id")
        }
)
@Check(constraints = "overtime_hours >= 0 AND overtime_rate_applied >= 0 AND amount >= 0")
public class OvertimeEntry {
    @Id
    @SequenceGenerator(
            name = "overtime_entry_sequence",
            sequenceName = "overtime_entry_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "overtime_entry_sequence",
            strategy = GenerationType.SEQUENCE
    )
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "worker_id", nullable = false)
    @ToString.Exclude
    private Worker worker;

    @NotNull
    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attendance_log_id", nullable = false)
    @ToString.Exclude
    private AttendanceLog attendanceLog;

    @NotNull
    @Column(name = "overtime_date", nullable = false)
    private LocalDate date;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "overtime_hours", nullable = false, precision = 6, scale = 2)
    private BigDecimal overtimeHours;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "overtime_rate_applied", nullable = false, precision = 10, scale = 2)
    private BigDecimal overtimeRateApplied;

    @NotNull
    @DecimalMin(value = "0.0")
    @Column(name = "amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal amount;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "settlement_status", nullable = false, length = 20)
    private SettlementStatus settlementStatus = SettlementStatus.PENDING;
}
