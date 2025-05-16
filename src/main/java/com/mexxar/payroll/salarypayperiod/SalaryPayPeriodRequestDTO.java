package com.mexxar.payroll.salarypayperiod;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.time.LocalDate;

public record SalaryPayPeriodRequestDTO(
        @NotNull(message = "Start date cannot be null")
        LocalDate startDate,

        @NotNull(message = "End date cannot be null")
        LocalDate endDate,

        @NotBlank(message = "Month cannot be blank")
        @Pattern(
                regexp = "\\d{4}-(JAN|FEB|MAR|APR|MAY|JUN|JUL|AUG|SEP|OCT|NOV|DEC)",
                message = "Month must be in the format YYYY-MMM")
        String monthOf,

        String year
)
{}
