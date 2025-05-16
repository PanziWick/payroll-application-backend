package com.mexxar.payroll.salary;

import com.mexxar.payroll.employee.EmployeeModel;
import com.mexxar.payroll.salaryallowance.SalaryAllowanceModel;
import com.mexxar.payroll.salarycommission.SalaryCommissionModel;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "Salary")
@Data
public class SalaryModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double basicSalary;
    private LocalDate startDate;
    private LocalDate endDate;

    @OneToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeModel employee;

    @OneToMany
    @JoinColumn(name = "salary_id")
    private List<SalaryAllowanceModel> salaryAllowances;

    @OneToMany
    @JoinColumn(name = "salary_id")
    private List<SalaryCommissionModel> salaryCommissions;
}
