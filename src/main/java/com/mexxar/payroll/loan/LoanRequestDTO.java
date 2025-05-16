package com.mexxar.payroll.loan;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record LoanRequestDTO(
        @NotNull(message = "Loan amount cannot be null")
        @Positive(message = "Loan amount must be greater than 0")
        Double loanAmount,

        @NotNull(message = "Interest rate cannot be null")
        @DecimalMin(value = "0.0", inclusive = true, message = "Interest rate must be at least 0.0%")
        @DecimalMax(value = "100.0", inclusive = true, message = "Interest rate cannot exceed 100%")
        Double interestRate,

        LocalDate startDate,

        LocalDate endDate,

        @NotNull(message = "Monthly installments cannot be null")
        @Positive(message = "Monthly installments must be greater than 0")
        Double monthlyInstallments,

        @NotNull(message = "Remaining amount cannot be null")
        @DecimalMin(value = "0.0", inclusive = true, message = "Remaining amount cannot be negative")
        Double remainingAmount,

        @NotNull(message = "Loan status cannot be null")
        LoanStatusEnum status,

        @NotNull(message = "Employee ID cannot be null")
        @Positive(message = "Employee ID must be a positive number")
        Long employeeId
)
{}
