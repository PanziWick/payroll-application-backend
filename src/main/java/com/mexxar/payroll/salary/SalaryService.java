package com.mexxar.payroll.salary;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.employee.EmployeeModel;
import com.mexxar.payroll.employee.EmployeeService;
import com.mexxar.payroll.salary.exception.SalaryNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

@Service
public class SalaryService {

    private final SalaryRepository salaryRepository;
    private final EmployeeService employeeService;

    public SalaryService(SalaryRepository salaryRepository, EmployeeService employeeService) {
        this.salaryRepository = salaryRepository;
        this.employeeService = employeeService;
    }

    private static final Logger logger = LogManager.getLogger(SalaryService.class);

    private static final String SALARY_NOT_FOUND_MSG = "Salary not found with Id: ";

    @Transactional
    public ApiResponseDTO<SalaryResponseDTO> createSalary(SalaryRequestDTO salaryRequestDTO) {
        logger.debug("Starting to create Salary for : {}", salaryRequestDTO);

        Instant startTime = Instant.now();
        EmployeeModel employeeModel = employeeService.getEmployeeModelById(salaryRequestDTO.employeeId());

        SalaryModel salary = new SalaryModel();
        salary.setBasicSalary(salaryRequestDTO.basicSalary());
        salary.setStartDate(salaryRequestDTO.startDate());
        salary.setEndDate(salaryRequestDTO.endDate());
        salary.setEmployee(employeeModel);

        SalaryModel savedSalary = salaryRepository.save(salary);
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Salary created successfully for salary id {} in {} ms", salary.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Salary Created Successfully", convertToResponseDTO(savedSalary));
    }

    public ApiResponseDTO<SalaryResponseDTO> getSalaryById(Long id) {
        if (id == null) {
            throw new NullPointerException("Salary ID cannot be null");
        }
        logger.debug("Starting to get Salary for : {}", id);

        Instant start = Instant.now();
        SalaryModel salary = salaryRepository.findById(id)
                .orElseThrow(() -> new SalaryNotFoundException(SALARY_NOT_FOUND_MSG + id));
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Salary fetched successfully for salary id {} in {} ms", salary.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Salary fetched successfully", convertToResponseDTO(salary));
    }

    public Optional<SalaryModel> getSalaryModelById(Long id) {
        return salaryRepository.findById(id);
    }

    public SalaryResponseDTO getSalaryByEmployeeId(Long employeeId) {
        logger.debug("Starting to get Salaries for Employee ID: {}", employeeId);

        Instant start = Instant.now();
        EmployeeModel employee = employeeService.getEmployeeModelById(employeeId);
        SalaryModel salary = salaryRepository.findByEmployeeId(employee.getId())
                .orElseThrow(() -> new SalaryNotFoundException(SALARY_NOT_FOUND_MSG + employeeId));
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Salary fetched successfully for Employee ID {} in {} ms", employeeId, timeElapsed.toMillis());

        return convertToResponseDTO(salary);
    }

    public ApiResponseDTO<Page<SalaryResponseDTO>> getAllSalaries(Pageable pageable) {
        if (pageable.isPaged()) {
            logger.info("Fetching all salaries with page size: {}", pageable.getPageSize());
        } else {
            logger.info("Fetching all salaries without pagination");
        }

        Instant start = Instant.now();
        Page<SalaryModel> salaries = salaryRepository.findAll(pageable);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Successfully fetched {} salaries in {} ms", salaries.getTotalElements(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Successfully Fetched All Salaries", salaries.map(this::convertToResponseDTO));
    }

    @Transactional
    public ApiResponseDTO<SalaryResponseDTO> updateSalary(Long id, SalaryRequestDTO salaryRequestDTO) {
        logger.debug("Starting to update Salary for : {}", salaryRequestDTO);

        Instant startTime = Instant.now();
        SalaryModel existingSalary = salaryRepository.findById(id)
                .orElseThrow(() -> new SalaryNotFoundException(SALARY_NOT_FOUND_MSG + id));

        existingSalary.setBasicSalary(salaryRequestDTO.basicSalary());
        existingSalary.setStartDate(salaryRequestDTO.startDate());
        existingSalary.setEndDate(salaryRequestDTO.endDate());

        SalaryModel updatedSalary = salaryRepository.save(existingSalary);
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Salary updated successfully for salary id {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Salary Updated Successfully", convertToResponseDTO(updatedSalary));
    }

    public ApiResponseDTO<Void> deleteSalary(Long id) {
        if (id == null) {
            throw new NullPointerException("Salary ID cannot be null");
        }
        logger.info("Starting to delete Salary for id: {}", id);

        Instant startTime = Instant.now();
        SalaryModel salary = salaryRepository.findById(id)
                .orElseThrow(() -> new SalaryNotFoundException(SALARY_NOT_FOUND_MSG + id));

        salaryRepository.delete(salary);
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Salary deleted successfully for salary id {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Salary Deleted Successfully", null);
    }

    private SalaryResponseDTO convertToResponseDTO(SalaryModel salary) {
        return new SalaryResponseDTO(
                salary.getId(),
                salary.getBasicSalary(),
                salary.getStartDate(),
                salary.getEndDate(),
                salary.getEmployee()
        );
    }
}
