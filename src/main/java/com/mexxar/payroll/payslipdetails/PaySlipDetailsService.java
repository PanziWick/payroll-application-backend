package com.mexxar.payroll.payslipdetails;

import com.mexxar.payroll.payslip.PaySlipModel;
import com.mexxar.payroll.payslip.PaySlipRepository;
import com.mexxar.payroll.payslip.exception.PaySlipNotFoundException;
import com.mexxar.payroll.payslipdetails.exception.PaySlipDetailsNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaySlipDetailsService {

    private final PaySlipDetailsRepository paySlipDetailsRepository;
    private final PaySlipRepository paySlipRepository;

    public PaySlipDetailsService(PaySlipDetailsRepository paySlipDetailsRepository, PaySlipRepository paySlipRepository) {
        this.paySlipDetailsRepository = paySlipDetailsRepository;
        this.paySlipRepository = paySlipRepository;
    }

    private static final Logger logger = LogManager.getLogger(PaySlipDetailsService.class);

    private static final String PAYSLIP_NOT_FOUND_WITH_ID = "PaySlip not found with ID: ";

    @Transactional
    public void createPaySlipDetails(PaySlipDetailsRequestDTO paySlipDetailsRequestDTO) {
        logger.info("Starting to create PaySlipDetails for PaySlip ID: {}", paySlipDetailsRequestDTO.paySlipId());

        PaySlipModel paySlip = paySlipRepository.findById(paySlipDetailsRequestDTO.paySlipId())
                .orElseThrow(() -> new PaySlipNotFoundException(PAYSLIP_NOT_FOUND_WITH_ID + paySlipDetailsRequestDTO.paySlipId()));

        PaySlipDetailsModel paySlipDetailsModel = new PaySlipDetailsModel();
        paySlipDetailsModel.setPaySlip(paySlip);
        paySlipDetailsModel.setLoanId(paySlipDetailsRequestDTO.loanId());
        paySlipDetailsModel.setAdvanceId(paySlipDetailsRequestDTO.advanceId());
        paySlipDetailsModel.setSalaryAllowanceId(paySlipDetailsRequestDTO.salaryAllowanceId());
        paySlipDetailsModel.setSalaryCommissionId(paySlipDetailsRequestDTO.salaryCommissionId());
        paySlipDetailsModel.setType(paySlipDetailsRequestDTO.type());
        paySlipDetailsModel.setDescription(paySlipDetailsRequestDTO.description());
        paySlipDetailsModel.setAmount(paySlipDetailsRequestDTO.amount());

        PaySlipDetailsModel savedDetails = paySlipDetailsRepository.save(paySlipDetailsModel);
        logger.info("PaySlipDetails created successfully with ID: {}", savedDetails.getId());
        convertToResponseDTO(savedDetails);
    }

    public PaySlipDetailsResponseDTO getPaySlipDetailsById(Long id) {
        logger.info("Fetching PaySlipDetails by ID: {}", id);

        PaySlipDetailsModel paySlipDetails = paySlipDetailsRepository.findById(id)
                .orElseThrow(() -> new PaySlipDetailsNotFoundException("PaySlipDetails not found with ID: " + id));
        logger.info("PaySlipDetails found for ID: {}", id);

        return convertToResponseDTO(paySlipDetails);
    }

    public void deletePaySlipDetails(Long id) {
        logger.info("Starting to delete PaySlipDetails for ID: {}", id);

        PaySlipDetailsModel paySlipDetails = paySlipDetailsRepository.findById(id)
                .orElseThrow(() -> new PaySlipDetailsNotFoundException("PaySlipDetails not found with ID: " + id));
        paySlipDetailsRepository.delete(paySlipDetails);
        logger.info("PaySlipDetails deleted successfully for ID: {}", id);
    }

    private PaySlipDetailsResponseDTO convertToResponseDTO(PaySlipDetailsModel paySlipDetailsModel) {
        return new PaySlipDetailsResponseDTO(
                paySlipDetailsModel.getId(),
                paySlipDetailsModel.getPaySlip().getId(),
                paySlipDetailsModel.getLoanId(),
                paySlipDetailsModel.getAdvanceId(),
                paySlipDetailsModel.getSalaryAllowanceId(),
                paySlipDetailsModel.getSalaryCommissionId(),
                paySlipDetailsModel.getType(),
                paySlipDetailsModel.getDescription(),
                paySlipDetailsModel.getAmount()
        );
    }
}
