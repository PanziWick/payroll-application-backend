package com.mexxar.payroll.address;

import com.mexxar.payroll.address.enums.TypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AddressRequestDTO(
        @NotNull(message = "Type cannot be null")
        TypeEnum type,

        @NotBlank(message = "Address line 1 cannot be empty")
        String addressLine1,

        @Size(max = 255, message = "Address line 2 cannot exceed 255 characters")
        String addressLine2,

        @NotBlank(message = "City cannot be empty")
        String city,

        @NotBlank(message = "Postcode cannot be empty")
        @Size(max = 20, message = "Postal code cannot exceed 20 characters")
        @Pattern(
                regexp = "^[A-Za-z0-9\\-\\s]+$",
                message = "Postal code contains invalid characters")
        String postalCode,

        @NotNull(message = "Employee ID cannot be null")
        Long employeeId
)
{}
