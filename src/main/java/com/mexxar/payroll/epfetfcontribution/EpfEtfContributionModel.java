package com.mexxar.payroll.epfetfcontribution;

import com.mexxar.payroll.employee.EmployeeModel;

import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodModel;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "EpfEtfContribution")
@Data
public class EpfEtfContributionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long payslipId;
    private Double epfContribution;
    private Double etfContribution;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeModel employee;

    @ManyToOne
    @JoinColumn(name = "salary_pay_period_id", nullable = false)
    private SalaryPayPeriodModel salaryPayPeriod;
}
