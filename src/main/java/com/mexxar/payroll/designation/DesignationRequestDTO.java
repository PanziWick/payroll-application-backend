package com.mexxar.payroll.designation;

import com.mexxar.payroll.common.enums.StatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record DesignationRequestDTO(
        @NotBlank(message = "Job title cannot be empty")
        @Size(min = 2, max = 100, message = "Job title must be between 2 and 100 characters")
        String jobTitle,

        @Size(max = 255, message = "Job description must not exceed 255 characters")
        String jobDescription,

        @NotNull(message = "Status is mandatory")
        StatusEnum status
)
{}
