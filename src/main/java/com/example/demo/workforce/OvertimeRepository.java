package com.example.demo.workforce;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface OvertimeRepository extends JpaRepository<OvertimeEntry, Long> {
    @Query("select coalesce(sum(o.overtimeHours), 0) from OvertimeEntry o where o.worker.id = :workerId and o.date between :from and :to")
    BigDecimal sumOvertimeHoursForWorkerBetween(@Param("workerId") Long workerId,
                                                @Param("from") LocalDate from,
                                                @Param("to") LocalDate to);

    List<OvertimeEntry> findByWorkerIdAndDateBetweenOrderByDateAsc(Long workerId, LocalDate from, LocalDate to);
}
