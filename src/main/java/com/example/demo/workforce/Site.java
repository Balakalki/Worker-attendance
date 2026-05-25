package com.example.demo.workforce;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;

@Getter
@Setter
@ToString
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(
        name = "sites",
        indexes = {
                @Index(name = "idx_sites_active", columnList = "active"),
                @Index(name = "idx_sites_location", columnList = "location")
        },
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_sites_name_location", columnNames = {"name", "location"})
        }
)
public class Site {
    @Id
    @SequenceGenerator(
            name = "site_sequence",
            sequenceName = "site_sequence",
            allocationSize = 1
    )
    @GeneratedValue(
            generator = "site_sequence",
            strategy = GenerationType.SEQUENCE
    )
    private Long id;

    @NotBlank
    @Column(name = "name", nullable = false, length = 150)
    private String name;

    @NotBlank
    @Column(name = "location", nullable = false, length = 255)
    private String location;

    @Column(name = "active", nullable = false)
    private Boolean active = true;
}
