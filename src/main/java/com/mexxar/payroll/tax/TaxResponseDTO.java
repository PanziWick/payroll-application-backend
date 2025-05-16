package com.mexxar.payroll.tax;

public record TaxResponseDTO(
        Long id,

        Double taxRate,

        Double minSalary,

        Double maxSalary
)
{}
