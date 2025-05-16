package com.mexxar.payroll.salaryallowance;

import jakarta.validation.constraints.*;

public record SalaryAllowanceRequestDTO(
        @NotNull(message = "Salary ID cannot be null")
        @Positive(message = "Salary ID must be a positive number")
        Long salaryId,

        @NotNull(message = "Allowance amount cannot be null")
        @DecimalMin(
                value = "0.0", inclusive = true,
                message = "Allowance amount must be zero or greater")
        Double amount,

        @NotNull(message = "Allowance type ID cannot be null")
        @Positive(message = "Allowance type ID must be a positive number")
        Long allowanceTypeId,

        Long salaryPayPeriodId
)
{}
