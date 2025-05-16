package com.mexxar.payroll.loanlog;

import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoanLogService {

    private final LoanLogRepository loanLogRepository;

    private static final Logger logger = LogManager.getLogger(LoanLogService.class);

    @Transactional
    public void createLoanLog(LoanLogRequestDTO logRequestDTO) {
        logger.info("Starting creation of Loan Log for Loan ID: {}, Employee ID: {}", logRequestDTO.loanId(), logRequestDTO.employeeId());

        LoanLogModel log = new LoanLogModel();

        log.setLoanId(logRequestDTO.loanId());

        log.setId(logRequestDTO.employeeId());
        log.setHoldStartDate(logRequestDTO.holdStartDate());
        log.setHoldEndDate(logRequestDTO.holdEndDate());
        log.setReason(logRequestDTO.reason());
        log.setIsHold(logRequestDTO.isHold());

        loanLogRepository.save(log);
        logger.info("Loan log successfully created for Loan ID: {} and Employee ID: {}", logRequestDTO.loanId(), logRequestDTO.employeeId());
    }

    public LoanLogModel getLatestLogByLoanId(Long loanId) {
        logger.info("Fetching the latest loan log for Loan ID: {}", loanId);

        LoanLogModel latestLog = loanLogRepository.findAll()
                .stream()
                .filter(log -> log.getLoanId().equals(loanId))
                .filter(log -> log.getIsHold().equals(true))
                .max(Comparator.comparing(LoanLogModel::getId))
                .orElse(null);

        if (latestLog == null) {
            logger.warn("No active loan logs found for Loan ID: {}", loanId);
        } else {
            logger.info("Latest loan log found with ID: {} for Loan ID: {}", latestLog.getId(), loanId);
        }

        return latestLog;
    }

    public List<LoanLogModel> getLoanLogsByLoanId(Long loanId) {
        return loanLogRepository.findLoanLogsByLoanId(loanId);
    }

    public List<LoanLogResponseDTO> convertToResponseDTO(List<LoanLogModel> loanHoldLogs) {
        logger.info("Converting list of LoanLogModel to LoanLogResponseDTO, Total logs: {}",

                loanHoldLogs == null ? 0 : loanHoldLogs.size());

        List<LoanLogResponseDTO> responseDTOs = Optional.ofNullable(loanHoldLogs)
                .orElse(Collections.emptyList())
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        logger.info("Successfully converted {} LoanLogModel to LoanLogResponseDTO", responseDTOs.size());

        return responseDTOs;
    }

    public LoanLogResponseDTO convertToResponseDTO(LoanLogModel loanHoldLog) {
        return new LoanLogResponseDTO(
                loanHoldLog.getId(),
                loanHoldLog.getLoanId(),
                loanHoldLog.getEmployeeId(),
                loanHoldLog.getHoldStartDate(),
                loanHoldLog.getHoldEndDate(),
                loanHoldLog.getReason(),
                loanHoldLog.getIsHold()
        );
    }
}
