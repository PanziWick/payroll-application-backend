package com.mexxar.payroll.loan;

import com.mexxar.payroll.loanlog.LoanLogRequestDTO;
import com.mexxar.payroll.loanlog.LoanLogService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class LoanReleaseScheduler {

    private final LoanRepository loanRepository;
    private final LoanLogService loanLogService;
    private final LoanService loanService;

    public LoanReleaseScheduler(LoanRepository loanRepository, LoanLogService loanLogService, LoanService loanService) {
        this.loanRepository = loanRepository;
        this.loanLogService = loanLogService;
        this.loanService = loanService;
    }

    private static final Logger logger = LogManager.getLogger(LoanReleaseScheduler.class);

    @Scheduled(cron = "0 0 0 * * *")
    public void releaseLoans() {
        logger.info("Starting the loan release job...");

        List<LoanModel> loans = loanService.getHeldLoans();

        for (LoanModel loan : loans) {
            if (loan.getStatus() == LoanStatusEnum.HOLD
                    && loan.getHoldEndDate().isBefore(LocalDate.now())) {

                logger.info("Releasing loan with ID: {}", loan.getId());

                // Update loan status
                loan.setStatus(LoanStatusEnum.ONGOING);
                loanRepository.save(loan);

                // Update log to indicate hold has ended
                LoanLogRequestDTO releaseLogRequest = new LoanLogRequestDTO(
                        loan.getId(),
                        loan.getEmployee().getId(),
                        loan.getHoldStartDate(),
                        LocalDate.now(),
                        "Loan hold released.",
                        false
                );
                loanLogService.createLoanLog(releaseLogRequest);

                logger.info("Loan with ID {} successfully released.", loan.getId());
            }
        }
        logger.info("Loan release job completed.");
    }
}
