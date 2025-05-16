package com.mexxar.payroll.salary;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record SalaryRequestDTO(
        @NotNull(message = "Basic salary cannot be null")
        @DecimalMin(value = "0.0", inclusive = true, message = "Basic salary cannot be negative")
        Double basicSalary,

        LocalDate startDate,

        LocalDate endDate,

        @NotNull(message = "Employee ID cannot be null")
        @Positive(message = "Employee ID must be a positive number")
        Long employeeId
)
{}
