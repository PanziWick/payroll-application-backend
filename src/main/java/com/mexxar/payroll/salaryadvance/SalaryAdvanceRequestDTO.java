package com.mexxar.payroll.salaryadvance;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record SalaryAdvanceRequestDTO(
        @NotNull(message = "Advance amount cannot be null")
        @DecimalMin(
                value = "0.01", inclusive = true,
                message = "Advance amount must be greater than zero")
        Double advanceAmount,

        LocalDate advanceDate,

        @NotNull(message = "Status cannot be null")
        SalaryAdvanceStatusEnum status,

        @NotNull(message = "Employee ID cannot be null")
        @Positive(message = "Employee ID must be a positive number")
        Long employeeId,

        Long salaryPayPeriodId
)
{}
