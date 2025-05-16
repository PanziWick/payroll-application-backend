package com.mexxar.payroll.salarycommission;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalaryCommissionRepository extends JpaRepository<SalaryCommissionModel, Long> {

    @Query("SELECT s FROM SalaryCommissionModel s WHERE s.salary.id = :salaryId " +
            "AND (:payPeriodId = 0 OR s.salaryPayPeriod.id = :payPeriodId) " +
            "AND s.commissionType.isLiableToTax = :isLiableToTax")
    List<SalaryCommissionModel> getCommissionByCriteria(@Param("salaryId") Long salaryId,
                                                        @Param("payPeriodId") Long payPeriodId,
                                                        @Param("isLiableToTax") Boolean isLiableToTax);
}
