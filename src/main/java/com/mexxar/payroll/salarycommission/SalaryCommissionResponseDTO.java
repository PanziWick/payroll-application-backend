package com.mexxar.payroll.salarycommission;

import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodResponseDTO;

public record SalaryCommissionResponseDTO(
        Long id,

        Double amount,

        Long commissionTypeId,

        String commissionTypeName,

        SalaryPayPeriodResponseDTO salaryPayPeriod
)
{}
