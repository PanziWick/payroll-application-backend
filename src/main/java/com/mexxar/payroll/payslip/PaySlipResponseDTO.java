package com.mexxar.payroll.payslip;

import com.mexxar.payroll.payslipdetails.PaySlipDetailsResponseDTO;
import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodResponseDTO;

import java.time.LocalDate;
import java.util.List;

public record PaySlipResponseDTO(
        Long id,

        Long salaryId,

        Double basicSalary,

        Double allowances,

        Double commission,

        PaySlipStatusEnum status,

        Double grossSalary,

        Double salaryAdvanceDeduction,

        Double loanDeduction,

        Double attendanceDeduction,

        Double taxDeduction,

        Double epfDeduction,

        Double leaveDeduction,

        Double netSalary,

        LocalDate startDate,

        LocalDate endDate,

        Long employeeId,

        Double taxExcludedAllowances,

        Double taxLiableAllowances,

        Double taxExcludedCommissions,

        Double taxLiableCommissions,

        List<PaySlipDetailsResponseDTO> details,

        SalaryPayPeriodResponseDTO salaryPayPeriod
)
{}
