package com.mexxar.payroll.salary;

import com.mexxar.payroll.employee.EmployeeModel;

import java.time.LocalDate;

public record SalaryResponseDTO(
        Long id,

        Double basicSalary,

        LocalDate startDate,

        LocalDate endDate,

        EmployeeModel employee
)
{}
