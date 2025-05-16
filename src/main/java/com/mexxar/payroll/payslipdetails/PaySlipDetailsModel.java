package com.mexxar.payroll.payslipdetails;

import com.mexxar.payroll.payslip.PaySlipModel;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "PaySlipDetails")
@Data
public class PaySlipDetailsModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Long loanId;
    private Long advanceId;
    private Long salaryAllowanceId;
    private Long salaryCommissionId;
    private PaySlipDetailsTypeEnum type;
    private String description;
    private Double amount;

    @ManyToOne
    @JoinColumn(name = "payslip_id", nullable = false)
    private PaySlipModel paySlip;
}