package com.mexxar.payroll.salaryadvance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SalaryAdvanceRepository extends JpaRepository<SalaryAdvanceModel, Long> {
    List<SalaryAdvanceModel> findByEmployeeId(Long employeeId);

    @Query("SELECT s FROM SalaryAdvanceModel s WHERE s.employee.id = :employeeId AND s.salaryPayPeriod.id = :payPeriodId AND s.status = 3")
    List<SalaryAdvanceModel> findPendingAdvancesByEmployeeIdAndSalaryPayPeriod(@Param("employeeId") Long employeeId,
                                                                               @Param("payPeriodId") Long payPeriodId);
}
