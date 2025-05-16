package com.mexxar.payroll.employee;

import com.mexxar.payroll.common.enums.StatusEnum;
import com.mexxar.payroll.department.DepartmentModel;
import com.mexxar.payroll.designation.DesignationModel;
import com.mexxar.payroll.employee.enums.GenderEnum;
import com.mexxar.payroll.employee.enums.MaritalEnum;

import java.time.LocalDate;

public record EmployeeResponseDTO(
        Long id,

        String firstName,

        String middleName,

        String lastName,

        String email,

        LocalDate dob,

        String contactNumber,

        LocalDate hireDate,

        String epfNumber,

        String nationalIdNumber,

        GenderEnum gender,

        MaritalEnum marital,

        DepartmentModel department,

        DesignationModel designation,

        StatusEnum status
)
{}
