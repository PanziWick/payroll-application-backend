package com.mexxar.payroll.payslip;

import java.time.LocalDate;

public record PaySlipFilterCriteria(
        Long employeeId,
        PaySlipStatusEnum status,
        LocalDate startDate,
        LocalDate endDate,
        Long payPeriodId
) {
}
