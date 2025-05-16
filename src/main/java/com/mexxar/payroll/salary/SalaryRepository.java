package com.mexxar.payroll.salary;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SalaryRepository extends JpaRepository<SalaryModel, Long> {
    Optional<SalaryModel> findByEmployeeId(Long employeeId);
}
