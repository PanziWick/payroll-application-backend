package com.mexxar.payroll.loan;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.employee.EmployeeModel;
import com.mexxar.payroll.employee.EmployeeService;
import com.mexxar.payroll.loan.exception.LoanException;
import com.mexxar.payroll.loan.exception.LoanNotFoundException;
import com.mexxar.payroll.loanlog.LoanLogModel;
import com.mexxar.payroll.loanlog.LoanLogRequestDTO;
import com.mexxar.payroll.loanlog.LoanLogService;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LoanService {

    private final LoanRepository loanRepository;
    private final EmployeeService employeeService;
    private final LoanLogService loanLogService;

    private static final Logger logger = LogManager.getLogger(LoanService.class);

    private static final String LOAN_NOT_FOUND_MSG = "Loan not found with Id: ";

    @Transactional
    public ApiResponseDTO<LoanResponseDTO> createLoan(LoanRequestDTO loanRequestDTO) {
        logger.debug("Starting to create Loan for : {}", loanRequestDTO);

        Instant startTime = Instant.now();

        try {
            EmployeeModel employeeModel = employeeService.getEmployeeModelById(loanRequestDTO.employeeId());

            LoanModel loan = new LoanModel();
            loan.setLoanAmount(loanRequestDTO.loanAmount());
            loan.setInterestRate(loanRequestDTO.interestRate());
            loan.setStartDate(loanRequestDTO.startDate());
            loan.setEndDate(loanRequestDTO.endDate());
            loan.setMonthlyInstallments(loanRequestDTO.monthlyInstallments());

            double remainingAmount = loanRequestDTO.loanAmount() + (loanRequestDTO.loanAmount() * loanRequestDTO.interestRate() * 0.01);
            loan.setRemainingAmount(remainingAmount);

            loan.setStatus(loanRequestDTO.status());
            loan.setEmployee(employeeModel);

            LoanModel savedLoan = loanRepository.save(loan);
            Instant endTime = Instant.now();
            Duration timeElapsed = Duration.between(startTime, endTime);
            logger.info("Loan created successfully for loan id {} in {} ms", loan.getId(), timeElapsed.toMillis());
            return new ApiResponseDTO<>("Loan Created Successfully", convertToResponseDTO(savedLoan));
        } catch (Exception e) {
            logger.error("Error creating loan: {}", e.getMessage());
            throw new LoanException("Failed to create loan. Please check the input data.");
        }
    }

    public ApiResponseDTO<LoanResponseDTO> getLoanById(Long id) {
        logger.debug("Starting to get Loan for : {}", id);

        LoanResponseDTO responseDTOs = loanRepository.findById(id)
                .map(this::convertToResponseDTO)
                .orElseThrow(() -> new LoanNotFoundException(LOAN_NOT_FOUND_MSG + id));

        return new ApiResponseDTO<>("Loan Fetched Successfully", responseDTOs);
    }

    public LoanModel getLoanModelById(Long id) {
        return loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(LOAN_NOT_FOUND_MSG + id));
    }

    public ApiResponseDTO<List<LoanResponseDTO>> getLoansByEmployeeId(Long employeeId) {
        logger.debug("Starting to get Loans for employee id: {}", employeeId);

        Instant startTime = Instant.now();

        List<LoanModel> loans = loanRepository.findByEmployeeId(employeeId);
        if (loans.isEmpty()) {
            logger.warn("No loans found for employee id: {}", employeeId);
        } else {
            logger.info("Retrieved {} loans for employee id: {}", loans.size(), employeeId);
        }

        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Retrieved loans for employee id {} in {} ms", employeeId, timeElapsed.toMillis());

        List<LoanResponseDTO> responseDTOs = loans.stream()
                .map(this::convertToResponseDTO)
                .toList();

        return new ApiResponseDTO<>("Loan Fetched Successfully For Employee ID", responseDTOs);
    }

    public List<LoanResponseDTO> getOngoingLoansByEmployeeId(Long employeeId) {
        List<LoanModel> loans = loanRepository.findOngoingLoansByEmployeeId(employeeId);

        return loans.stream()
                .map(this::convertToResponseDTO)
                .toList();
    }

    public ApiResponseDTO<Page<LoanResponseDTO>> getAllLoans(Pageable pageable) {
        logger.debug("Starting to get all Loans");

        Instant startTime = Instant.now();

        try {
            Page<LoanResponseDTO> loans = loanRepository.findAll(pageable).map(this::convertToResponseDTO);
            Instant endTime = Instant.now();
            Duration timeElapsed = Duration.between(startTime, endTime);
            logger.info("Retrieved all loans in {} ms", timeElapsed.toMillis());
            return new ApiResponseDTO<>("Successfully Fetched All Loans", loans);
        } catch (Exception e) {
            logger.error("Error retrieving all loans: {}", e.getMessage());
            throw new LoanException("Failed to retrieve loan records.");
        }
    }

    public List<LoanModel> getHeldLoans() {
        return loanRepository.findHoldLoans();
    }

    @Transactional
    public ApiResponseDTO<LoanResponseDTO> updateLoan(Long id, LoanRequestDTO loanRequestDTO) {
        logger.debug("Starting to update Loan for id: {}", id);

        Instant startTime = Instant.now();

        LoanModel existingLoan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(LOAN_NOT_FOUND_MSG + id));

        try {
            existingLoan.setLoanAmount(loanRequestDTO.loanAmount());
            existingLoan.setInterestRate(loanRequestDTO.interestRate());
            existingLoan.setStartDate(loanRequestDTO.startDate());
            existingLoan.setEndDate(loanRequestDTO.endDate());
            existingLoan.setMonthlyInstallments(loanRequestDTO.monthlyInstallments());

            double remainingAmount = loanRequestDTO.loanAmount() + (loanRequestDTO.loanAmount() * loanRequestDTO.interestRate() * 0.01);
            existingLoan.setRemainingAmount(remainingAmount);

            existingLoan.setStatus(loanRequestDTO.status());

            LoanModel updatedLoan = loanRepository.save(existingLoan);
            Instant endTime = Instant.now();
            Duration timeElapsed = Duration.between(startTime, endTime);
            logger.info("Loan updated successfully for loan id {} in {} ms", id, timeElapsed.toMillis());
            return new ApiResponseDTO<>("Loan Updated Successfully", convertToResponseDTO(updatedLoan));
        } catch (Exception e) {
            logger.error("Error updating loan with ID {}: {}", id, e.getMessage());
            throw new LoanException("Failed to update loan. Please check the input data.");
        }
    }

    public ApiResponseDTO<Void> deleteLoan(Long id) {
        logger.info("Starting to delete Loan for id: {}", id);

        Instant startTime = Instant.now();

        LoanModel loan = loanRepository.findById(id)
                .orElseThrow(() -> new LoanNotFoundException(LOAN_NOT_FOUND_MSG + id));

        try {
            loanRepository.delete(loan);
            Instant endTime = Instant.now();
            Duration timeElapsed = Duration.between(startTime, endTime);
            logger.info("Loan deleted successfully for loan id {} in {} ms", id, timeElapsed.toMillis());
        } catch (Exception e) {
            logger.error("Error deleting loan with ID {}: {}", id, e.getMessage());
            throw new LoanException("Failed to delete loan. Please try again.");
        }

        return new ApiResponseDTO<>("Loan Deleted Successfully", null);
    }

    public void updateLoanRemainingAmount(Long id, double paidAmount) {
        Optional<LoanModel> loanModel = loanRepository.findById(id);
        loanModel.ifPresent(loan -> {
            if (loan.getRemainingAmount() > 0) {
                loan.setRemainingAmount(loan.getRemainingAmount() - paidAmount);
                loanRepository.save(loan);
            }
        });
    }

    public ApiResponseDTO<LoanResponseDTO> holdLoanDeduction(LoanLogRequestDTO loanLogRequestDTO) {
        LoanModel loan = loanRepository.findById(loanLogRequestDTO.loanId())
                .orElseThrow(() -> new LoanNotFoundException(LOAN_NOT_FOUND_MSG + loanLogRequestDTO.loanId()));

        loan.setStatus(LoanStatusEnum.HOLD);
        loan.setHoldStartDate(loanLogRequestDTO.holdStartDate());
        loan.setHoldEndDate(loanLogRequestDTO.holdEndDate());
        loanRepository.save(loan);

        loanLogService.createLoanLog(loanLogRequestDTO);

        return new ApiResponseDTO<>("Loan Deduction Hold", convertToResponseDTO(loan));
    }

    public ApiResponseDTO<LoanResponseDTO> releaseLoanHold(Long loanId) {
        LoanModel loan = loanRepository.findById(loanId)
                .orElseThrow(() -> new LoanNotFoundException(LOAN_NOT_FOUND_MSG + loanId));

        if (!loan.getStatus().equals(LoanStatusEnum.HOLD)) {
            throw new LoanException("Loan is not currently on hold.");
        }

        loan.setStatus(LoanStatusEnum.ONGOING);
        loan.setHoldStartDate(null);
        loan.setHoldEndDate(null);
        loanRepository.save(loan);

        // Create release log entry
        LoanLogModel latestLog = loanLogService.getLatestLogByLoanId(loanId);
        if (latestLog == null) {
            throw new LoanException("No valid hold log found for the loan.");
        }

        LoanLogRequestDTO releaseLogRequest = new LoanLogRequestDTO(
                loanId,
                loan.getEmployee().getId(),
                latestLog.getHoldStartDate(),
                LocalDate.now(),
                "Loan hold released.",
                false
        );

        loanLogService.createLoanLog(releaseLogRequest);

        return new ApiResponseDTO<>("Loan Deduction Release", convertToResponseDTO(loan));
    }

    private LoanResponseDTO convertToResponseDTO(LoanModel loan) {
        return new LoanResponseDTO(
                loan.getId(),
                loan.getLoanAmount(),
                loan.getInterestRate(),
                loan.getStartDate(),
                loan.getEndDate(),
                loan.getMonthlyInstallments(),
                loan.getRemainingAmount(),
                loan.getStatus(),
                loan.getHoldStartDate(),
                loan.getHoldEndDate(),
                loan.getEmployee(),
                loanLogService.getLoanLogsByLoanId(loan.getId())
        );
    }
}
