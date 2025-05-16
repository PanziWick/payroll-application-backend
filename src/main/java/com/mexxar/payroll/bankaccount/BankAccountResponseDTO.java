package com.mexxar.payroll.bankaccount;

import com.mexxar.payroll.employee.EmployeeModel;

public record BankAccountResponseDTO(
        Long id,
        String accountHolderName,
        String bankName,
        String accountNumber,
        String branchName,
        AccountTypeEnum accountType,
        EmployeeModel employee
)
{}
