package com.mexxar.payroll.payslipdetails;

import jakarta.validation.constraints.*;

public record PaySlipDetailsRequestDTO(
        @NotNull(message = "Pay slip ID cannot be null")
        @Positive(message = "Pay slip ID must be a positive number")
        Long paySlipId,

        @Positive(message = "Loan ID must be a positive number")
        Long loanId,

        @Positive(message = "Advance ID must be a positive number")
        Long advanceId,

        @Positive(message = "Salary allowance ID must be a positive number")
        Long salaryAllowanceId,

        @Positive(message = "Salary commission ID must be a positive number")
        Long salaryCommissionId,

        @NotNull(message = "Type cannot be null")
        PaySlipDetailsTypeEnum type,

        @NotBlank(message = "Description cannot be blank")
        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description,

        @NotNull(message = "Amount cannot be null")
        @DecimalMin(
                value = "0.0", inclusive = true,
                message = "Amount cannot be negative")
        Double amount
)
{}
