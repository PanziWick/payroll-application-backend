package com.mexxar.payroll.epfetfcontribution;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.employee.EmployeeModel;
import com.mexxar.payroll.employee.EmployeeService;
import com.mexxar.payroll.epfetfcontribution.exception.EpfEtfContributionNotFoundException;
import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodModel;
import com.mexxar.payroll.salarypayperiod.SalaryPayPeriodService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class EpfEtfContributionService {

    private final EpfEtfContributionRepository repo;
    private final EmployeeService employeeService;
    private final SalaryPayPeriodService salaryPayPeriodService;

    public EpfEtfContributionService(EpfEtfContributionRepository repo, EmployeeService employeeService, SalaryPayPeriodService salaryPayPeriodService) {
        this.repo = repo;
        this.employeeService = employeeService;
        this.salaryPayPeriodService = salaryPayPeriodService;
    }

    private static final Logger logger = LogManager.getLogger(EpfEtfContributionService.class);

    private static final String NO_CONTRIBUTIONS_FOUND_FOR_EMPLOYEE_ID = "No contributions found for employee ID: ";
    private static final String NO_CONTRIBUTIONS_FOUND_FOR_THE_MONTH = "No contributions found for the month: ";
    private static final String CONTRIBUTION_NOT_FOUND_WITH_ID = "Contribution not found with ID: ";

    @Transactional
    public void createEpfEtfContribution(EpfEtfContributionRequestDTO requestDTO) {
        logger.info("Starting to create EPF/ETF contribution for employee ID: {}", requestDTO.employeeId());

        Instant start = Instant.now();

        SalaryPayPeriodModel payPeriod = salaryPayPeriodService.getPayPeriodModelById(requestDTO.payPeriodId());

        EpfEtfContributionModel contribution = new EpfEtfContributionModel();
        contribution.setPayslipId(requestDTO.paySlipId());
        contribution.setEpfContribution(requestDTO.epfContribution());
        contribution.setEtfContribution(requestDTO.etfContribution());
        contribution.setSalaryPayPeriod(payPeriod);

        EmployeeModel employee = employeeService.getEmployeeModelById(requestDTO.employeeId());
        contribution.setEmployee(employee);

        repo.save(contribution);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("EPF/ETF contribution created successfully for employee ID: {} in {} ms", requestDTO.employeeId(), timeElapsed.toMillis());
    }

    public ApiResponseDTO<List<EpfEtfContributionResponseDTO>> getEpfEtfContributionsByEmployeeId(Long employeeId) {
        logger.info("Fetching EPF/ETF contributions for employee ID: {}", employeeId);

        Instant start = Instant.now();

        List<EpfEtfContributionModel> contributions = repo.findByEmployeeId(employeeId);
        if (contributions.isEmpty()) {
            throw new EpfEtfContributionNotFoundException(NO_CONTRIBUTIONS_FOUND_FOR_EMPLOYEE_ID + employeeId);
        }

        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Successfully fetched {} contributions for employee ID: {} in {} ms", contributions.size(), employeeId, timeElapsed.toMillis());

        List<EpfEtfContributionResponseDTO> responseDTOs = contributions
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return new ApiResponseDTO<>("Contributions Fetched Successfully For Employee ID", responseDTOs);
    }

    public ApiResponseDTO<List<EpfEtfContributionResponseDTO>> getEpfEtfContributionsByMonthOf(Long payPeriodId) {
        logger.info("Fetching EPF/ETF contributions for the salary pay period: {}", payPeriodId);

        Instant start = Instant.now();

        List<EpfEtfContributionModel> contributions = repo.findBySalaryPayPeriod(payPeriodId);
        if (contributions.isEmpty()) {
            throw new EpfEtfContributionNotFoundException(NO_CONTRIBUTIONS_FOUND_FOR_THE_MONTH + payPeriodId);
        }

        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Successfully fetched {} contributions for the month: {} in {} ms", contributions.size(), payPeriodId, timeElapsed.toMillis());

        List<EpfEtfContributionResponseDTO> responseDTOs = contributions
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return new ApiResponseDTO<>("Contributions Fetched Successfully For MonthOf", responseDTOs);
    }

    public ApiResponseDTO<EpfEtfContributionResponseDTO> getContributionById(Long id) {
        logger.info("Fetching EPF/ETF contribution by ID: {}", id);

        Instant start = Instant.now();

        EpfEtfContributionModel model = repo.findById(id)
                .orElseThrow(() -> new EpfEtfContributionNotFoundException(CONTRIBUTION_NOT_FOUND_WITH_ID + id));

        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Successfully fetched contribution with ID: {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Contribution Fetched Successfully", convertToResponseDTO(model));
    }

    public ApiResponseDTO<List<EpfEtfContributionResponseDTO>> getAllContributions() {
        logger.info("Fetching all EPF/ETF contributions.");

        Instant start = Instant.now();

        List<EpfEtfContributionResponseDTO> contributions = repo.findAll()
                .stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Successfully fetched {} contributions in {} ms", contributions.size(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Successfully Fetched All Contributions", contributions);
    }

    public void deleteContribution(Long id) {
        logger.info("Starting to delete EPF/ETF contribution with ID: {}", id);

        if (!repo.existsById(id)) {
            throw new EpfEtfContributionNotFoundException(CONTRIBUTION_NOT_FOUND_WITH_ID + id);
        }

        Instant start = Instant.now();
        repo.deleteById(id);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Successfully deleted contribution with ID: {} in {} ms", id, timeElapsed.toMillis());
    }

    private EpfEtfContributionResponseDTO convertToResponseDTO(EpfEtfContributionModel savedModel) {
        return new EpfEtfContributionResponseDTO(
                savedModel.getId(),
                savedModel.getPayslipId(),
                savedModel.getEpfContribution(),
                savedModel.getEtfContribution(),
                savedModel.getEmployee().getId(),
                salaryPayPeriodService.convertToResponseDTO(savedModel.getSalaryPayPeriod())
        );
    }
}
