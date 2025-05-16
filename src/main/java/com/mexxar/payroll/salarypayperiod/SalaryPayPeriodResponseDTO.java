package com.mexxar.payroll.salarypayperiod;

import java.time.LocalDate;

public record SalaryPayPeriodResponseDTO(
        Long id,
        LocalDate startDate,
        LocalDate endDate,
        String monthOf,
        String year
)
{}
