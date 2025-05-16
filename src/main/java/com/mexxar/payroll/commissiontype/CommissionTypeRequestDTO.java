package com.mexxar.payroll.commissiontype;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CommissionTypeRequestDTO(
        @NotBlank(message = "Name cannot be empty")
        @Size(max = 100, message = "Name cannot exceed 100 characters")
        String name,

        Boolean isLiableToTax,

        @Size(max = 255, message = "Description cannot exceed 255 characters")
        String description
)
{}
