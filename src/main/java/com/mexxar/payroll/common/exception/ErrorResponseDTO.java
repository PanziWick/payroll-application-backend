package com.mexxar.payroll.common.exception;

import java.time.LocalDateTime;

public record ErrorResponseDTO(
        int status,
        String errorMessage,
        String errorType,
        LocalDateTime timestamp
)
{}
