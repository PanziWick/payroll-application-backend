package com.mexxar.payroll.employeeleave;

import com.mexxar.payroll.leave.LeaveTypeEnum;

public record EmployeeRemainingLeaveDTO(
        Long leavePolicyId,
        String name,
        int year,
        LeaveTypeEnum leaveType,
        int maxDays,
        Double remainingLeaves
)
{}
