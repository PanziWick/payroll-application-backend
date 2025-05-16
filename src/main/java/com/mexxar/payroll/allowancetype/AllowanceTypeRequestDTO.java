package com.mexxar.payroll.allowancetype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AllowanceTypeRequestDTO(
        @NotBlank(message = "Name cannot be empty")
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,

        Boolean isFixed,

        Boolean isLiableToTax,

        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description
)
{}
