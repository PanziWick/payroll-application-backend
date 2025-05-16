package com.mexxar.payroll.loanlog;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record LoanLogRequestDTO(
        @NotNull(message = "Loan ID cannot be null")
        @Positive(message = "Loan ID must be a positive number")
        Long loanId,

        @NotNull(message = "Employee ID cannot be null")
        @Positive(message = "Employee ID must be a positive number")
        Long employeeId,

        @NotNull(message = "Hold start date cannot be null if hold is applied")
        @PastOrPresent(message = "Hold start date cannot be in the future")
        LocalDate holdStartDate,

        @NotNull(message = "Hold end date cannot be null if hold is applied")
        @FutureOrPresent(message = "Hold end date must be today or in the future")
        LocalDate holdEndDate,

        @NotBlank(message = "Reason cannot be blank")
        @Size(max = 255, message = "Reason cannot exceed 255 characters")
        String reason,

        @NotNull(message = "Hold status cannot be null")
        Boolean isHold
)
{}
