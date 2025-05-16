package com.mexxar.payroll.commissiontype;

public record CommissionTypeResponseDTO(
        Long id,
        String name,
        Boolean isLiableToTax,
        String description
)
{}
