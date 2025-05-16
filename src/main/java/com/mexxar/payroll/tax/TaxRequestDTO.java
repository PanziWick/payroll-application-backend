package com.mexxar.payroll.tax;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public record TaxRequestDTO(
        @NotNull(message = "Tax rate cannot be null")
        @DecimalMin(value = "0.0", inclusive = true, message = "Tax rate must be 0% or higher")
        @DecimalMax(value = "100.0", inclusive = true, message = "Tax rate must be 100% or lower")
        Double taxRate,

        @NotNull(message = "Minimum salary cannot be null")
        @DecimalMin(value = "0.0", inclusive = true, message = "Minimum salary must be 0 or higher")
        Double minSalary,

        Double maxSalary
)
{}
