package com.mexxar.payroll.salarycommission;

import com.mexxar.payroll.commissiontype.CommissionTypeModel;
import com.mexxar.payroll.salary.SalaryModel;
import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodModel;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "SalaryCommission")
@Data
public class SalaryCommissionModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "salary_id", nullable = false)
    private SalaryModel salary;

    private Double amount;

    @ManyToOne
    @JoinColumn(name = "commission_type_id")
    private CommissionTypeModel commissionType;

    @ManyToOne
    @JoinColumn(name = "salary_pay_period_id", nullable = false)
    private SalaryPayPeriodModel salaryPayPeriod;
}
