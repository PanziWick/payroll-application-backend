package com.mexxar.payroll.loan;

import com.mexxar.payroll.employee.EmployeeModel;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "Loan")
@Data
public class LoanModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Double loanAmount;
    private Double interestRate;
    private LocalDate startDate;
    private LocalDate endDate;
    private Double monthlyInstallments;
    private Double remainingAmount;

    private LoanStatusEnum status;

    private LocalDate holdStartDate;
    private LocalDate holdEndDate;

    @ManyToOne
    @JoinColumn(name = "employee_id", nullable = false)
    private EmployeeModel employee;

//    @OneToMany(mappedBy = "loan", cascade = CascadeType.ALL)
//    private List<LoanLogModel> loanHoldLogs;
}
