package com.mexxar.payroll.designationmanagementhistory;

import java.util.Date;

public record DesignationManagementHistoryResponseDTO(
        Long employeeId,
        String employeeFirstName,
        String employeeLastName,
        String previousDesignation,
        String currentDesignation,
        String previousDepartment,
        String currentDepartment,
        Date changeDate
) {
}
