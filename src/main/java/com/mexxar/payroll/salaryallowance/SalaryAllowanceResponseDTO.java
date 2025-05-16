package com.mexxar.payroll.salaryallowance;

import com.mexxar.payroll.allowancetype.AllowanceTypeResponseDTO;
import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodResponseDTO;

public record SalaryAllowanceResponseDTO(
        Long id,

        Long salaryId,

        Double amount,

        AllowanceTypeResponseDTO allowanceType,

        SalaryPayPeriodResponseDTO salaryPayPeriod
)
{}
