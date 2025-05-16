package com.mexxar.payroll.salarypayperiod;

import com.mexxar.payroll.payslip.PaySlipModel;
import com.mexxar.payroll.salaryallowance.SalaryAllowanceModel;
import com.mexxar.payroll.salarycommission.SalaryCommissionModel;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "SalaryPayPeriod")
@Data
public class SalaryPayPeriodModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate startDate;
    private LocalDate endDate;
    private String monthOf;
    private String year;

    @OneToMany(mappedBy = "salaryPayPeriod", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalaryAllowanceModel> salaryAllowanceModel;

    @OneToMany(mappedBy = "salaryPayPeriod", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SalaryCommissionModel> salaryCommissionModel;

    @OneToMany(mappedBy = "salaryPayPeriod", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PaySlipModel> paySlips;
}
