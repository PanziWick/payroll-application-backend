package com.mexxar.payroll.loanlog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LoanLogRepository extends JpaRepository<LoanLogModel, Long> {
    List<LoanLogModel> findLoanLogsByLoanId(Long loanId);
}
