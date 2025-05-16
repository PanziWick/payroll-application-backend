package com.mexxar.payroll.epfetfcontribution;

import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodResponseDTO;

public record EpfEtfContributionResponseDTO(
        Long id,

        Long paySlipId,

        Double epfContribution,

        Double etfContribution,

        Long employeeId,

        SalaryPayPeriodResponseDTO salaryPayPeriod
)
{}
