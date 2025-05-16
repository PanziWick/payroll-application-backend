package com.mexxar.payroll.bankaccount;

import com.mexxar.payroll.employee.EmployeeModel;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "BankAccount")
@Data
public class BankAccountModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String accountHolderName;
    private String bankName;
    private String accountNumber;
    private String branchName;
    private AccountTypeEnum accountType;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeModel employee;
}
