package com.mexxar.payroll.role;

import com.mexxar.payroll.permission.PermissionResponseDTO;

import java.util.List;

public record RoleResponseDTO(
        Long id,

        String name,

        List<PermissionResponseDTO> permissions
)
{}
