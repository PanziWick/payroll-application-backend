package com.mexxar.payroll.bankaccount;

import com.mexxar.payroll.employee.EmployeeModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

interface BankAccountRepository extends JpaRepository<BankAccountModel, Long> {
    boolean existsByEmployeeAndAccountNumber(EmployeeModel employee, String accountNumber);

    List<BankAccountModel> findByEmployeeAndAccountType(EmployeeModel employee, AccountTypeEnum accountType);
}
