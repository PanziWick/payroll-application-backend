package com.mexxar.payroll.salaryallowance;

import com.mexxar.payroll.allowancetype.AllowanceTypeModel;
import com.mexxar.payroll.allowancetype.AllowanceTypeService;
import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.salary.SalaryModel;
import com.mexxar.payroll.salary.SalaryService;
import com.mexxar.payroll.salaryallowance.exception.SalaryAllowanceNotFoundException;
import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodModel;
import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class SalaryAllowanceService {

    private final SalaryAllowanceRepository salaryAllowanceRepository;
    private final SalaryService salaryService;
    private final AllowanceTypeService allowanceTypeService;
    private final SalaryPayPeriodService salaryPayPeriodService;

    public SalaryAllowanceService(SalaryAllowanceRepository salaryAllowanceRepository, SalaryService salaryService, AllowanceTypeService allowanceTypeService, SalaryPayPeriodService salaryPayPeriodService) {
        this.salaryAllowanceRepository = salaryAllowanceRepository;
        this.salaryService = salaryService;
        this.allowanceTypeService = allowanceTypeService;
        this.salaryPayPeriodService = salaryPayPeriodService;
    }

    private static final Logger logger = LogManager.getLogger(SalaryAllowanceService.class);

    private static final String SALARY_ALLOWANCE_NOT_FOUND_MSG = "SalaryAllowance not found with ID: ";
    private static final String SALARY_NOT_FOUND_MSG = "Salary not found for ID: ";
    private static final String ALLOWANCE_TYPE_NOT_FOUND_MSG = "Allowance Type not found for ID: ";

    @Transactional
    public ApiResponseDTO<SalaryAllowanceResponseDTO> createSalaryAllowance(SalaryAllowanceRequestDTO salaryAllowanceRequestDTO) {
        logger.debug("Creating SalaryAllowance for request: {}", salaryAllowanceRequestDTO);

        Instant start = Instant.now();
        SalaryModel salary = salaryService.getSalaryModelById(salaryAllowanceRequestDTO.salaryId())
                .orElseThrow(() -> new SalaryAllowanceNotFoundException(SALARY_NOT_FOUND_MSG + salaryAllowanceRequestDTO.salaryId()));

        AllowanceTypeModel allowanceType = allowanceTypeService.getAllowanceTypeModelById(salaryAllowanceRequestDTO.allowanceTypeId())
                .orElseThrow(() -> new SalaryAllowanceNotFoundException(ALLOWANCE_TYPE_NOT_FOUND_MSG + salaryAllowanceRequestDTO.allowanceTypeId()));

        SalaryPayPeriodModel payPeriod = salaryPayPeriodService.getPayPeriodModelById(salaryAllowanceRequestDTO.salaryPayPeriodId());

        SalaryAllowanceModel salaryAllowance = new SalaryAllowanceModel();
        salaryAllowance.setSalary(salary);
        salaryAllowance.setAmount(salaryAllowanceRequestDTO.amount());
        salaryAllowance.setAllowanceType(allowanceType);
        salaryAllowance.setSalaryPayPeriod(payPeriod);

        SalaryAllowanceModel savedSalaryAllowance = salaryAllowanceRepository.save(salaryAllowance);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("SalaryAllowance created successfully with ID: {} in {} ms", savedSalaryAllowance.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Salary Allowance Created Successfully", convertToResponseDTO(savedSalaryAllowance));
    }

    public ApiResponseDTO<SalaryAllowanceResponseDTO> getSalaryAllowanceById(Long id) {
        logger.debug("Fetching SalaryAllowance with ID: {}", id);

        Instant start = Instant.now();
        SalaryAllowanceModel salaryAllowance = salaryAllowanceRepository.findById(id)
                .orElseThrow(() -> new SalaryAllowanceNotFoundException(SALARY_ALLOWANCE_NOT_FOUND_MSG + id));
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("SalaryAllowance fetched successfully for ID: {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Salary Allowance Fetched Successfully", convertToResponseDTO(salaryAllowance));
    }

    public ApiResponseDTO<List<SalaryAllowanceResponseDTO>> getAllSalaryAllowances() {
        logger.info("Starting to get all salary allowances");

        Instant startTime = Instant.now();
        List<SalaryAllowanceResponseDTO> salaryAllowances = salaryAllowanceRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .toList();
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Successfully fetched {} salary allowances in {} ms", salaryAllowances.size(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Successfully Fetched All Salary allowances", salaryAllowances);
    }

    @Transactional
    public ApiResponseDTO<SalaryAllowanceResponseDTO> updateSalaryAllowance(Long id, SalaryAllowanceRequestDTO salaryAllowanceRequestDTO) {
        logger.debug("Updating SalaryAllowance with ID: {}", id);

        Instant start = Instant.now();
        SalaryAllowanceModel existingAllowance = salaryAllowanceRepository.findById(id)
                .orElseThrow(() -> new SalaryAllowanceNotFoundException(SALARY_ALLOWANCE_NOT_FOUND_MSG + id));

        AllowanceTypeModel allowanceType = allowanceTypeService.getAllowanceTypeModelById(salaryAllowanceRequestDTO.allowanceTypeId())
                .orElseThrow(() -> new SalaryAllowanceNotFoundException(ALLOWANCE_TYPE_NOT_FOUND_MSG + salaryAllowanceRequestDTO.allowanceTypeId()));

        SalaryPayPeriodModel payPeriod = salaryPayPeriodService.getPayPeriodModelById(salaryAllowanceRequestDTO.salaryPayPeriodId());

        existingAllowance.setAmount(salaryAllowanceRequestDTO.amount());
        existingAllowance.setAllowanceType(allowanceType);
        existingAllowance.setSalaryPayPeriod(payPeriod);

        SalaryAllowanceModel updatedAllowance = salaryAllowanceRepository.save(existingAllowance);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("SalaryAllowance updated successfully for ID: {} in {} ms", updatedAllowance.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Salary Allowance Updated Successfully", convertToResponseDTO(updatedAllowance));
    }


    public ApiResponseDTO<Void> deleteSalaryAllowance(Long id) {
        logger.info("Deleting SalaryAllowance with ID: {}", id);

        Instant start = Instant.now();
        SalaryAllowanceModel salaryAllowance = salaryAllowanceRepository.findById(id)
                .orElseThrow(() -> new SalaryAllowanceNotFoundException(SALARY_ALLOWANCE_NOT_FOUND_MSG + id));

        salaryAllowanceRepository.delete(salaryAllowance);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("SalaryAllowance deleted successfully with ID: {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Salary Allowance deleted successfully", null);
    }

    public List<SalaryAllowanceModel> getAllowancesByCriteria(long salaryId, long payPeriodId, Boolean isFixed, Boolean isLiableToTax){
        return salaryAllowanceRepository.getAllowancesByCriteria(salaryId, payPeriodId, isFixed, isLiableToTax);
    }

    private SalaryAllowanceResponseDTO convertToResponseDTO(SalaryAllowanceModel salaryAllowance) {
        return new SalaryAllowanceResponseDTO(
                salaryAllowance.getId(),
                salaryAllowance.getSalary().getId(),
                salaryAllowance.getAmount(),
                allowanceTypeService.convertToResponseDTO(salaryAllowance.getAllowanceType()),
                salaryPayPeriodService.convertToResponseDTO(salaryAllowance.getSalaryPayPeriod())
        );
    }
}
