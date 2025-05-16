package com.mexxar.payroll.address;


import com.mexxar.payroll.address.enums.TypeEnum;

public record AddressResponseDTO (
        Long id,

        TypeEnum type,

        String addressLine1,

        String addressLine2,

        String city,

        String postalCode,

        Long employeeId
)
{}
