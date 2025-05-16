package com.mexxar.payroll.salaryadvance;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.employee.EmployeeModel;
import com.mexxar.payroll.employee.EmployeeService;
import com.mexxar.payroll.salaryadvance.exception.SalaryAdvanceException;
import com.mexxar.payroll.salaryadvance.exception.SalaryAdvanceNotFoundException;
import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodModel;
import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

@Service
public class SalaryAdvanceService {

    private final SalaryAdvanceRepository salaryAdvanceRepository;
    private final EmployeeService employeeService;
    private final SalaryPayPeriodService salaryPayPeriodService;

    public SalaryAdvanceService(SalaryAdvanceRepository salaryAdvanceRepository, EmployeeService employeeService, SalaryPayPeriodService salaryPayPeriodService) {
        this.salaryAdvanceRepository = salaryAdvanceRepository;
        this.employeeService = employeeService;
        this.salaryPayPeriodService = salaryPayPeriodService;
    }

    private static final Logger logger = LogManager.getLogger(SalaryAdvanceService.class);

    private static final String ADVANCE_NOT_FOUND_MSG = "Salary Advance not found with ID: ";

    @Transactional
    public ApiResponseDTO<SalaryAdvanceResponseDTO> createSalaryAdvance(SalaryAdvanceRequestDTO request) {
        logger.debug("Starting to create Salary Advance for request: {}", request);

        Instant startTime = Instant.now();

        SalaryPayPeriodModel payPeriod = salaryPayPeriodService.getPayPeriodModelById(request.salaryPayPeriodId());

        try {
            EmployeeModel employee = employeeService.getEmployeeModelById(request.employeeId());

            SalaryAdvanceModel advance = new SalaryAdvanceModel();
            advance.setAdvanceAmount(request.advanceAmount());
            advance.setAdvanceDate(request.advanceDate());
            advance.setStatus(request.status());
            advance.setEmployee(employee);
            advance.setSalaryPayPeriod(payPeriod);

            SalaryAdvanceModel savedAdvance = salaryAdvanceRepository.save(advance);

            Instant endTime = Instant.now();
            Duration timeElapsed = Duration.between(startTime, endTime);
            logger.info("Salary Advance created successfully with ID {} in {} ms", savedAdvance.getId(), timeElapsed.toMillis());
            return new ApiResponseDTO<>("Salary Advance Created Successfully", convertToResponseDTO(savedAdvance));
        } catch (Exception e) {
            logger.error("Error creating salary advance: {}", e.getMessage());
            throw new SalaryAdvanceException("Failed to create salary advance. Please check the input data.");
        }
    }

    public ApiResponseDTO<SalaryAdvanceResponseDTO> getSalaryAdvanceById(Long id) {
        logger.debug("Starting to get Salary Advance for ID: {}", id);

        SalaryAdvanceResponseDTO responseDTO = salaryAdvanceRepository.findById(id)
                .map(this::convertToResponseDTO)
                .orElseThrow(() -> new SalaryAdvanceNotFoundException(ADVANCE_NOT_FOUND_MSG + id));

        return new ApiResponseDTO<>("Salary Advance Fetched Successfully", responseDTO);
    }

    public ApiResponseDTO<List<SalaryAdvanceResponseDTO>> getSalaryAdvancesByEmployeeId(Long employeeId) {
        logger.debug("Starting to get Salary Advances for Employee ID: {}", employeeId);

        Instant startTime = Instant.now();
        List<SalaryAdvanceModel> advances = salaryAdvanceRepository.findByEmployeeId(employeeId);
        if (advances.isEmpty()) {
            logger.warn("No Salary Advances found for Employee ID: {}", employeeId);
        } else {
            logger.info("Retrieved {} Salary Advances for Employee ID: {}", advances.size(), employeeId);
        }

        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Retrieved Salary Advances for Employee ID {} in {} ms", employeeId, timeElapsed.toMillis());

        List<SalaryAdvanceResponseDTO> responseDTOs = advances.stream()
                .map(this::convertToResponseDTO)
                .toList();

        return new ApiResponseDTO<>("Salary Advances Fetched Successfully For Employee ID", responseDTOs);
    }

    public List<SalaryAdvanceResponseDTO> getPendingSalaryAdvancesByMonthAndEmployeeId(Long employeeId, Long payPeriodId) {
        List<SalaryAdvanceModel> pendingAdvances = salaryAdvanceRepository.findPendingAdvancesByEmployeeIdAndSalaryPayPeriod(
                employeeId,
                payPeriodId
        );
        return pendingAdvances.stream()
                .map(this::convertToResponseDTO)
                .toList();    }

    public ApiResponseDTO<Page<SalaryAdvanceResponseDTO>> getAllSalaryAdvances(Pageable pageable) {
        logger.debug("Starting to retrieve all salary advances");

        Instant startTime = Instant.now();
        Page<SalaryAdvanceModel> salaryAdvancesPage = salaryAdvanceRepository.findAll(pageable);
        Page<SalaryAdvanceResponseDTO> responseDTOPage = salaryAdvancesPage.map(this::convertToResponseDTO);
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Retrieved all salary advances in {} ms", timeElapsed.toMillis());

        return new ApiResponseDTO<>("Successfully Fetched All Salary Advances", responseDTOPage);
    }

    @Transactional
    public ApiResponseDTO<SalaryAdvanceResponseDTO> updateSalaryAdvance(Long id, SalaryAdvanceRequestDTO request) {
        logger.debug("Starting to update Salary Advance for ID: {}", id);

        Instant startTime = Instant.now();
        SalaryAdvanceModel existingAdvance = salaryAdvanceRepository.findById(id)
                .orElseThrow(() -> new SalaryAdvanceNotFoundException(ADVANCE_NOT_FOUND_MSG + id));

        SalaryPayPeriodModel payPeriod = salaryPayPeriodService.getPayPeriodModelById(request.salaryPayPeriodId());

        try {
            EmployeeModel employee = employeeService.getEmployeeModelById(request.employeeId());

            existingAdvance.setAdvanceAmount(request.advanceAmount());
            existingAdvance.setAdvanceDate(request.advanceDate());
            existingAdvance.setStatus(request.status());
            existingAdvance.setEmployee(employee);
            existingAdvance.setSalaryPayPeriod(payPeriod);
            SalaryAdvanceModel updatedAdvance = salaryAdvanceRepository.save(existingAdvance);

            Instant endTime = Instant.now();
            Duration timeElapsed = Duration.between(startTime, endTime);
            logger.info("Salary Advance updated successfully for ID {} in {} ms", id, timeElapsed.toMillis());
            return new ApiResponseDTO<>("Salary Advance Updated Successfully", convertToResponseDTO(updatedAdvance));
        } catch (Exception e) {
            logger.error("Error updating Salary Advance with ID {}: {}", id, e.getMessage());
            throw new SalaryAdvanceException("Failed to update salary advance. Please check the input data.");
        }
    }

    public ApiResponseDTO<Void> deleteSalaryAdvance(Long id) {
        logger.info("Starting to delete Salary Advance for ID: {}", id);

        Instant startTime = Instant.now();
        SalaryAdvanceModel advance = salaryAdvanceRepository.findById(id)
                .orElseThrow(() -> new SalaryAdvanceNotFoundException(ADVANCE_NOT_FOUND_MSG + id));

        try {
            salaryAdvanceRepository.delete(advance);
            Instant endTime = Instant.now();
            Duration timeElapsed = Duration.between(startTime, endTime);
            logger.info("Salary Advance deleted successfully for ID {} in {} ms", id, timeElapsed.toMillis());
            return new ApiResponseDTO<>("Salary Advance Deleted Successfully", null);
        } catch (Exception e) {
            logger.error("Error deleting Salary Advance with ID {}: {}", id, e.getMessage());
            throw new SalaryAdvanceException("Failed to delete salary advance. Please try again.");
        }
    }

    private SalaryAdvanceResponseDTO convertToResponseDTO(SalaryAdvanceModel advance) {
        return new SalaryAdvanceResponseDTO(
                advance.getId(),
                advance.getAdvanceAmount(),
                advance.getAdvanceDate(),
                advance.getStatus(),
                advance.getEmployee(),
                salaryPayPeriodService.convertToResponseDTO(advance.getSalaryPayPeriod())
        );
    }
}
