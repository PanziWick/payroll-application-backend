package com.mexxar.payroll.bankaccount;

public record BankAccountRequestDTO(
        String accountHolderName,
        String bankName,
        String accountNumber,
        String branchName,
        AccountTypeEnum accountType,
        Long employeeId
)
{}
