package com.mexxar.payroll.leave;

public record LeavePolicyRequestDTO(
        String name,
        int year,
        LeaveTypeEnum leaveType,
        int maxDays,
        boolean carryForwardAllowed
)
{}
