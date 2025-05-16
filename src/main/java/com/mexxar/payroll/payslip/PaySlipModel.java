package com.mexxar.payroll.payslip;

import com.mexxar.payroll.employee.EmployeeModel;
import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodModel;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "PaySlip")
@Data
public class PaySlipModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long salaryId;
    private Double basicSalary;
    private Double allowances;
    private Double commission;
    private PaySlipStatusEnum status;
    private Double grossSalary;
    private Double salaryAdvanceDeduction;
    private Double loanDeduction;
    private Double attendanceDeduction;
    private Double taxDeduction;
    private Double epfDeduction;
    private Double leaveDeduction;
    private Double netSalary;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double taxExcludedAllowances;
    private Double taxLiableAllowances;
    private Double taxExcludedCommissions;
    private Double taxLiableCommissions;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeModel employee;

    @ManyToOne
    @JoinColumn(name = "salary_pay_period_id", nullable = false)
    private SalaryPayPeriodModel salaryPayPeriod;
}
