package com.mexxar.payroll.salarycommission;

import jakarta.validation.constraints.*;

public record SalaryCommissionRequestDTO(
        @NotNull(message = "Salary ID cannot be null")
        @Positive(message = "Salary ID must be a positive number")
        Long salaryId,

        @NotNull(message = "Commission amount cannot be null")
        @DecimalMin(
                value = "0.0", inclusive = true,
                message = "Commission amount must be zero or greater")
        Double amount,

        @NotNull(message = "Commission type ID cannot be null")
        @Positive(message = "Commission type ID must be a positive number")
        Long commissionTypeId,

        Long salaryPayPeriodId
)
{}
