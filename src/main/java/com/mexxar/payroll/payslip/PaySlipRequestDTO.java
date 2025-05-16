package com.mexxar.payroll.payslip;

import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record PaySlipRequestDTO(
        @NotNull(message = "Pay slip status cannot be null")
        PaySlipStatusEnum status,

        @NotNull(message = "Attendance deduction cannot be null")
        @DecimalMin(
                value = "0.0", inclusive = true,
                message = "Attendance deduction cannot be negative")
        Double attendanceDeduction,

        LocalDate startDate,

        LocalDate endDate,

        @NotNull(message = "Employee ID cannot be null")
        @Positive(message = "Employee ID must be a positive number")
        Long employeeId,

        Long payPeriodId
)
{}
