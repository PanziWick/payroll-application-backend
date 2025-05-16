package com.mexxar.payroll.loanlog;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Table(name = "LoanLog")
@Data
public class LoanLogModel {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //    @ManyToOne
//    @JoinColumn(name = "loan_id", nullable = false)
    private Long loanId;

    //    @ManyToOne
//    @JoinColumn(name = "employee_id", nullable = false)
    private Long employeeId;

    private LocalDate holdStartDate;
    private LocalDate holdEndDate;
    private String reason;

    @Column(nullable = false, columnDefinition = "BOOLEAN DEFAULT false")
    private Boolean isHold = false;
}
