package com.mexxar.payroll.designationmanagementhistory;

public record DesignationManagementHistoryRequestDTO(
        Long employeeId,
        Long designationId,
        Long departmentId
) {
}
