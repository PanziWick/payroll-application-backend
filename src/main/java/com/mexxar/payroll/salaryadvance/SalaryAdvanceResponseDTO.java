package com.mexxar.payroll.salaryadvance;

import com.mexxar.payroll.employee.EmployeeModel;
import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodResponseDTO;

import java.time.LocalDate;

public record SalaryAdvanceResponseDTO(
        Long id,

        Double advanceAmount,

        LocalDate advanceDate,

        SalaryAdvanceStatusEnum status,

        EmployeeModel employee,

        SalaryPayPeriodResponseDTO salaryPayPeriod
)
{}
