package com.mexxar.payroll.epfetfcontribution;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

interface EpfEtfContributionRepository extends JpaRepository<EpfEtfContributionModel, Long> {
    List<EpfEtfContributionModel> findByEmployeeId(Long employeeId);

    @Query("SELECT e FROM EpfEtfContributionModel e WHERE e.salaryPayPeriod.id = :payPeriodId")
    List<EpfEtfContributionModel> findBySalaryPayPeriod(Long payPeriodId);
}
