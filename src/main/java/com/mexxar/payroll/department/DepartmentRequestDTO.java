package com.mexxar.payroll.department;

import com.mexxar.payroll.common.enums.StatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DepartmentRequestDTO(
        @NotBlank(message = "Department name cannot be empty")
        @Size(min = 2, max = 50, message = "Department name must be between 2 and 50 characters")
        String name,

        @NotNull(message = "Status cannot be null")
        StatusEnum status
)
{}
