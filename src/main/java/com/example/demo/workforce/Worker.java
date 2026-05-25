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

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
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
}
