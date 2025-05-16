package com.mexxar.payroll.role;

import jakarta.validation.constraints.*;

import java.util.List;

public record RoleRequestDTO(
        @NotBlank(message = "Role name cannot be blank")
        @Size(max = 50, message = "Role name cannot exceed 50 characters")
        String name,

        List<Long> permissionIdList
)
{}
