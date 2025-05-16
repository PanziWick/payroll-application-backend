package com.mexxar.payroll.employeeleave;

import java.time.LocalDate;

public record EmployeeLeaveRequestDTO(
        LocalDate startDate,
        LocalDate endDate,
        Double numberOfDays,
        EmployeeLeaveEnum status,
        String approvedBy,
        Long employeeId,
        Long leavePolicyId
)
{}
