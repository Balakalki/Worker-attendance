package com.example.demo.workforce;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceLog, Long> {
    Optional<AttendanceLog> findByWorkerIdAndClockOutAtIsNull(Long workerId);

    Page<AttendanceLog> findByWorkerIdAndClockInAtBetween(Long workerId, Instant from, Instant to, Pageable pageable);
}
