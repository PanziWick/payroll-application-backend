package com.mexxar.payroll.salaryallowance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalaryAllowanceRepository extends JpaRepository<SalaryAllowanceModel, Long> {

    @Query("SELECT s FROM SalaryAllowanceModel s WHERE s.salary.id = :salaryId " +
            "AND (:payPeriodId = 0 OR s.salaryPayPeriod.id = :payPeriodId) " +
            "AND s.allowanceType.isFixed = :isFixed " +
            "AND s.allowanceType.isLiableToTax = :isLiableToTax")
    List<SalaryAllowanceModel> getAllowancesByCriteria(@Param("salaryId") Long salaryId,
                                                       @Param("payPeriodId") Long payPeriodId,
                                                       @Param("isFixed") Boolean isFixed,
                                                       @Param("isLiableToTax") Boolean isLiableToTax);
}
