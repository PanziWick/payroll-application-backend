package com.mexxar.payroll.salaryadvance;

import com.mexxar.payroll.employee.EmployeeModel;
import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodModel;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "SalaryAdvance")
@Data
public class SalaryAdvanceModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Double advanceAmount;
    private LocalDate advanceDate;
    private SalaryAdvanceStatusEnum status;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeModel employee;

    @ManyToOne
    @JoinColumn(name = "salary_pay_period_id", nullable = false)
    private SalaryPayPeriodModel salaryPayPeriod;
}
