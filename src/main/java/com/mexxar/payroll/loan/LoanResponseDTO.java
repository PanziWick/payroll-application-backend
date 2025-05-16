package com.mexxar.payroll.loan;

import com.mexxar.payroll.employee.EmployeeModel;
import com.mexxar.payroll.loanlog.LoanLogModel;

import java.time.LocalDate;
import java.util.List;

public record LoanResponseDTO(
        Long id,

        Double loanAmount,

        Double interestRate,

        LocalDate startDate,

        LocalDate endDate,

        Double monthlyInstallments,

        Double remainingAmount,

        LoanStatusEnum status,

        LocalDate holdStartDate,

        LocalDate holdEndDate,

        EmployeeModel employee,

        List<LoanLogModel> loanHoldLogs
) {
}
