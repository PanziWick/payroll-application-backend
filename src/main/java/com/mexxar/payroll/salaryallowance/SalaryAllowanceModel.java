package com.mexxar.payroll.salaryallowance;

import com.mexxar.payroll.allowancetype.AllowanceTypeModel;
import com.mexxar.payroll.salary.SalaryModel;
import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodModel;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "SalaryAllowance")
@Data
public class SalaryAllowanceModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "salary_id", nullable = false)
    private SalaryModel salary;

    private Double amount;

    @ManyToOne
    @JoinColumn(name = "allowance_type_id")
    private AllowanceTypeModel allowanceType;

    @ManyToOne
    @JoinColumn(name = "salary_pay_period_id")
    private SalaryPayPeriodModel salaryPayPeriod;
}
