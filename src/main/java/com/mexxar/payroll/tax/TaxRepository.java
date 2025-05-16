package com.mexxar.payroll.tax;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaxRepository extends JpaRepository<TaxModel, Long> {
    List<TaxModel> findByMinSalaryLessThanEqual(Double salary);
}
