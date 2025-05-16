package com.mexxar.payroll.payslipdetails;

public record PaySlipDetailsResponseDTO(
        Long id,

        Long paySlipId,

        Long loanId,

        Long advanceId,

        Long salaryAllowanceId,

        Long salaryCommissionId,

        PaySlipDetailsTypeEnum type,

        String description,

        Double amount
)
{}
