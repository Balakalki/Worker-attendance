package com.example.demo.workforce;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.Instant;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<AttendanceLog, Long> {
    @EntityGraph(attributePaths = {"worker", "site"})
    Optional<AttendanceLog> findByWorkerIdAndClockOutAtIsNull(Long workerId);

    @EntityGraph(attributePaths = {"worker", "site"})
    Page<AttendanceLog> findByWorkerIdAndClockInAtBetween(Long workerId, Instant from, Instant to, Pageable pageable);

    @EntityGraph(attributePaths = {"worker", "site"})
    @Query("select a from AttendanceLog a where (:workerId is null or a.worker.id = :workerId) and a.clockInAt >= :from and a.clockInAt <= :to order by a.clockInAt desc")
    Page<AttendanceLog> searchAttendanceLogs(@Param("workerId") Long workerId,
                                             @Param("from") Instant from,
                                             @Param("to") Instant to,
                                             Pageable pageable);
}
