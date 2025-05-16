package com.mexxar.payroll.permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PermissionRequestDTO(
        @NotBlank(message = "Permission name cannot be blank")
        @Size(max = 50, message = "Permission name cannot exceed 50 characters")
        String name,

        @Size(max = 255, message = "Permission description cannot exceed 255 characters")
        String description
)
{}
