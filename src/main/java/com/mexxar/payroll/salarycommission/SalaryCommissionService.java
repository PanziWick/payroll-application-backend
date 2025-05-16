package com.mexxar.payroll.salarycommission;

import com.mexxar.payroll.commissiontype.CommissionTypeModel;
import com.mexxar.payroll.commissiontype.CommissionTypeService;
import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.salary.SalaryModel;
import com.mexxar.payroll.salary.SalaryService;
import com.mexxar.payroll.salarycommission.exception.SalaryCommissionNotFoundException;
import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodModel;
import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;

@Service
public class SalaryCommissionService {

    private final SalaryCommissionRepository salaryCommissionRepository;
    private final SalaryService salaryService;
    private final CommissionTypeService commissionTypeService;
    private final SalaryPayPeriodService salaryPayPeriodService;

    public SalaryCommissionService(SalaryCommissionRepository salaryCommissionRepository, SalaryService salaryService, CommissionTypeService commissionTypeService, SalaryPayPeriodService salaryPayPeriodService) {
        this.salaryCommissionRepository = salaryCommissionRepository;
        this.salaryService = salaryService;
        this.commissionTypeService = commissionTypeService;
        this.salaryPayPeriodService = salaryPayPeriodService;
    }

    private static final Logger logger = LogManager.getLogger(SalaryCommissionService.class);

    private static final String SALARY_COMMISSION_NOT_FOUND_MSG = "SalaryCommission not found with ID: ";
    private static final String SALARY_NOT_FOUND_MSG = "Salary not found for ID: ";
    private static final String COMMISSION_TYPE_NOT_FOUND_MSG = "Commission Type not found for ID: ";

    @Transactional
    public ApiResponseDTO<SalaryCommissionResponseDTO> createSalaryCommission(SalaryCommissionRequestDTO salaryCommissionRequestDTO) {
        logger.debug("Creating SalaryCommission for request: {}", salaryCommissionRequestDTO);

        Instant start = Instant.now();
        SalaryModel salary = salaryService.getSalaryModelById(salaryCommissionRequestDTO.salaryId())
                .orElseThrow(() -> new SalaryCommissionNotFoundException(SALARY_NOT_FOUND_MSG + salaryCommissionRequestDTO.salaryId()));

        CommissionTypeModel commissionType = commissionTypeService.getCommissionTypeModelById(salaryCommissionRequestDTO.commissionTypeId())
                .orElseThrow(() -> new SalaryCommissionNotFoundException(COMMISSION_TYPE_NOT_FOUND_MSG + salaryCommissionRequestDTO.commissionTypeId()));

        SalaryPayPeriodModel payPeriod = salaryPayPeriodService.getPayPeriodModelById(salaryCommissionRequestDTO.salaryPayPeriodId());

        SalaryCommissionModel commission = new SalaryCommissionModel();
        commission.setSalary(salary);
        commission.setAmount(salaryCommissionRequestDTO.amount());
        commission.setCommissionType(commissionType);
        commission.setSalaryPayPeriod(payPeriod);

        SalaryCommissionModel savedCommission = salaryCommissionRepository.save(commission);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("SalaryCommission created successfully with ID: {} in {} ms", savedCommission.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Salary Commission Created Successfully", convertToResponseDTO(savedCommission));
    }

    public ApiResponseDTO<SalaryCommissionResponseDTO> getSalaryCommissionById(Long id) {
        logger.debug("Fetching SalaryCommission with ID: {}", id);

        Instant start = Instant.now();
        SalaryCommissionModel commission = salaryCommissionRepository.findById(id)
                .orElseThrow(() -> new SalaryCommissionNotFoundException(SALARY_COMMISSION_NOT_FOUND_MSG + id));
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("SalaryCommission fetched successfully for ID: {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Salary Commission Fetched Successfully", convertToResponseDTO(commission));
    }

    public ApiResponseDTO<List<SalaryCommissionResponseDTO>> getAllSalaryCommissions() {
        logger.info("Starting to get all salary commissions");

        Instant startTime = Instant.now();
        List<SalaryCommissionResponseDTO> commissions = salaryCommissionRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Successfully fetched {} salary commissions in {} ms", commissions.size(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Successfully Fetched All Salary Commission", commissions);
    }

    public List<SalaryCommissionResponseDTO> getAllSalaryCommissionByCriteria(Long salaryId, Long payPeriod, Boolean isLiableToTax) {
        logger.info("Starting to fetch SalaryCommissions for salary ID: {} and month: {}", salaryId, payPeriod);

        Instant start = Instant.now();
        Optional<SalaryModel> salary = salaryService.getSalaryModelById(salaryId);

        List<SalaryCommissionModel> salaryCommissions = salary.map(salaryModel ->
                salaryCommissionRepository.getCommissionByCriteria(salaryId, payPeriod, isLiableToTax)
        ).orElseThrow(() -> new SalaryCommissionNotFoundException(SALARY_NOT_FOUND_MSG + salaryId));

        List<SalaryCommissionResponseDTO> responseDTOs = salaryCommissions.stream()
                .map(this::convertToResponseDTO)
                .toList();
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Successfully fetched {} SalaryCommissions for salary ID: {} in {} ms", responseDTOs.size(), salaryId, timeElapsed.toMillis());

        return responseDTOs;
    }

    @Transactional
    public ApiResponseDTO<SalaryCommissionResponseDTO> updateSalaryCommission(Long id, SalaryCommissionRequestDTO salaryCommissionRequestDTO) {
        logger.debug("Updating SalaryCommission with ID: {}", id);

        Instant start = Instant.now();
        SalaryCommissionModel existingCommission = salaryCommissionRepository.findById(id)
                .orElseThrow(() -> new SalaryCommissionNotFoundException(SALARY_COMMISSION_NOT_FOUND_MSG + id));

        CommissionTypeModel commissionType = commissionTypeService.getCommissionTypeModelById(salaryCommissionRequestDTO.commissionTypeId())
                .orElseThrow(() -> new SalaryCommissionNotFoundException(COMMISSION_TYPE_NOT_FOUND_MSG + salaryCommissionRequestDTO.commissionTypeId()));

        SalaryPayPeriodModel payPeriod = salaryPayPeriodService.getPayPeriodModelById(salaryCommissionRequestDTO.salaryPayPeriodId());

        existingCommission.setAmount(salaryCommissionRequestDTO.amount());
        existingCommission.setCommissionType(commissionType);
        existingCommission.setSalaryPayPeriod(payPeriod);

        SalaryCommissionModel updatedCommission = salaryCommissionRepository.save(existingCommission);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("SalaryCommission updated successfully with ID: {} in {} ms", updatedCommission.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Salary Commission Updated Successfully", convertToResponseDTO(updatedCommission));
    }

    public ApiResponseDTO<Void> deleteSalaryCommission(Long id) {
        logger.info("Deleting SalaryCommission with ID: {}", id);

        Instant start = Instant.now();
        SalaryCommissionModel commission = salaryCommissionRepository.findById(id)
                .orElseThrow(() -> new SalaryCommissionNotFoundException(SALARY_COMMISSION_NOT_FOUND_MSG + id));

        salaryCommissionRepository.delete(commission);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("SalaryCommission deleted successfully with ID: {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Salary Commission Deleted Successfully", null);
    }

    private SalaryCommissionResponseDTO convertToResponseDTO(SalaryCommissionModel salaryCommission) {
        return new SalaryCommissionResponseDTO(
                salaryCommission.getId(),
                salaryCommission.getAmount(),
                salaryCommission.getCommissionType().getId(),
                salaryCommission.getCommissionType().getName(),
                salaryPayPeriodService.convertToResponseDTO(salaryCommission.getSalaryPayPeriod())
        );
    }
}
