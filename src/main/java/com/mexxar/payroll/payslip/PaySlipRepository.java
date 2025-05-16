package com.mexxar.payroll.payslip;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PaySlipRepository extends JpaRepository<PaySlipModel, Long>, JpaSpecificationExecutor<PaySlipModel> {

    @Query("SELECT " +
            "CASE " +
            "WHEN SUM(p.grossSalary) BETWEEN 0 AND 1200000 THEN '0 - 1,200,000' " +
            "WHEN SUM(p.grossSalary) BETWEEN 1200001 AND 1700000 THEN '1,200,001 - 1,700,000' " +
            "WHEN SUM(p.grossSalary) BETWEEN 1700001 AND 2200000 THEN '1,700,001 - 2,200,000' " +
            "WHEN SUM(p.grossSalary) BETWEEN 2200001 AND 2700000 THEN '2,200,001 - 2,700,000' " +
            "WHEN SUM(p.grossSalary) BETWEEN 2700001 AND 3200000 THEN '2,700,001 - 3,200,000' " +
            "WHEN SUM(p.grossSalary) BETWEEN 3200001 AND 3700000 THEN '3,200,001 - 3,700,000' " +
            "ELSE '3,700,000+' END, COUNT(DISTINCT p.employee.id) " +
            "FROM PaySlipModel p " +
            "WHERE p.salaryPayPeriod.id IN :payPeriodIdList " +
            "GROUP BY p.employee.id")
    List<Object[]> getEmployeeCountByAnnualSalaryRange(@Param("payPeriodIdList") List<Long> payPeriodIdList);

    @Query("SELECT " +
        "sp.monthOf, " +
        "SUM(ps.taxExcludedAllowances + ps.taxExcludedCommissions), " +
        "SUM(ps.taxLiableAllowances + ps.taxLiableCommissions), " +
        "SUM(ps.taxDeduction), " +
        "SUM(ps.netSalary) " +
        "FROM PaySlipModel ps " +
        "JOIN ps.salaryPayPeriod sp " +
        "WHERE sp.year = :year " +
        "GROUP BY sp.id")
    List<TaxAndRemunerationSummaryDTO> getMonthlyTaxAndRemunerationSummary(@Param("year") Long year);

}
