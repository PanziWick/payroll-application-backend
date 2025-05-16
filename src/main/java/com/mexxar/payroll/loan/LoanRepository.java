package com.mexxar.payroll.loan;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

interface LoanRepository extends JpaRepository<LoanModel, Long> {
    Page<LoanModel> findAll(Pageable pageable);

    List<LoanModel> findByEmployeeId(Long employeeId);

    @Query("SELECT l FROM LoanModel l WHERE l.employee.id = :employeeId AND l.status = 3")
    List<LoanModel> findOngoingLoansByEmployeeId(Long employeeId);

    @Query("SELECT l FROM LoanModel l WHERE l.status = 4")
    List<LoanModel> findHoldLoans();
}
