package com.mexxar.payroll.salarypayperiod;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface SalaryPayPeriodRepository extends JpaRepository<SalaryPayPeriodModel, Long> {
    boolean existsByStartDateAndEndDateAndMonthOf(LocalDate startDate, LocalDate endDate, String monthOf);

    @Query("SELECT p FROM SalaryPayPeriodModel p WHERE p.monthOf = :year%")
    List<SalaryPayPeriodModel> findAllByYear(@Param("year") Long year);
}
