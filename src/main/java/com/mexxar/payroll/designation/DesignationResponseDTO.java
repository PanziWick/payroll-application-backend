package com.mexxar.payroll.designation;

import com.mexxar.payroll.common.enums.StatusEnum;

public record DesignationResponseDTO(
        Long id,

        String jobTitle,

        String jobDescription,

        StatusEnum status
)
{}
