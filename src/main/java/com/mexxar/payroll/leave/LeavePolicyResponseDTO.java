package com.mexxar.payroll.leave;


public record LeavePolicyResponseDTO(
        Long id,
        String name,
        int year,
        LeaveTypeEnum leaveType,
        int maxDays,
        boolean carryForwardAllowed
)
{}
