package com.mexxar.payroll.loanlog;

import java.time.LocalDate;

public record LoanLogResponseDTO(
        Long id,

        Long loanId,

        Long employeeId,

        LocalDate holdStartDate,

        LocalDate holdEndDate,

        String reason,

        Boolean isHold
)
{}
