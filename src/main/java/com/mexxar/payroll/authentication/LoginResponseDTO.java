package com.mexxar.payroll.authentication;

public record LoginResponseDTO(
        String username,

        String message,

        String token,

        String refreshToken
)
{}
