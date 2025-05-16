package com.mexxar.payroll.authentication.security;

public record TokenRefreshRequestDTO(
        String refreshToken
)
{}
