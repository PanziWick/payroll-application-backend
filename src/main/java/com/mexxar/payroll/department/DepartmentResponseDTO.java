package com.mexxar.payroll.department;

import com.mexxar.payroll.common.enums.StatusEnum;

public record DepartmentResponseDTO(
        Long id,

        String name,

        StatusEnum status
)
{}
