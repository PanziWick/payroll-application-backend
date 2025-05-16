package com.mexxar.payroll.employee;

import com.mexxar.payroll.common.enums.StatusEnum;

import java.time.LocalDate;

public record EmployeeFilterCriteria(
        String contactNumber,
        LocalDate hireFrom,
        LocalDate hireTo,
        StatusEnum status,
        Long departmentId,
        Long designationId,
        String searchQuery
) {
}
