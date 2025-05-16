package com.mexxar.payroll.allowancetype;

public record AllowanceTypeResponseDTO(
        Long id,

        String name,

        Boolean isFixed,

        Boolean isLiableToTax,

        String description
)
{}
