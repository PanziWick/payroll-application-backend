package com.mexxar.payroll.user;

import java.util.List;

public record UserResponseDTO(
        Long id,

        String userName,

        String email,

        String contactNumber,

        String status,

        List<String> roles
)
{}
