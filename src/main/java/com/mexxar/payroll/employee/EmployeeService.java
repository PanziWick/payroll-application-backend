package com.mexxar.payroll.employee;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.common.enums.StatusEnum;
import com.mexxar.payroll.department.DepartmentModel;
import com.mexxar.payroll.department.DepartmentService;
import com.mexxar.payroll.designation.DesignationModel;
import com.mexxar.payroll.designation.DesignationService;
import com.mexxar.payroll.designationmanagementhistory.DesignationManagementHistoryRequestDTO;
import com.mexxar.payroll.designationmanagementhistory.DesignationManagementHistoryResponseDTO;
import com.mexxar.payroll.designationmanagementhistory.DesignationManagementHistoryService;
import com.mexxar.payroll.designationmanagementhistory.exception.DesignationManagementHistoryException;
import com.mexxar.payroll.employee.exception.EmployeeException;
import com.mexxar.payroll.employee.exception.EmployeeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentService departmentService;
    private final DesignationService designationService;
    private final DesignationManagementHistoryService designationManagementHistoryService;

    private static final Logger logger = LogManager.getLogger(EmployeeService.class);

    private static final String EMPLOYEE_WITH_SAME_DATA_FOUND_MSG = "Employee with the same email already exists";
    private static final String EMPLOYEE_NOT_FOUND_MSG = "Employee not found with id: ";

    @Transactional
    public ApiResponseDTO<EmployeeResponseDTO> createEmployee(EmployeeRequestDTO employeeRequestDTO) {
        logger.debug("Starting to create employee for : {}", employeeRequestDTO);

        if (employeeRepository.existsByEmail(employeeRequestDTO.email())) {
            throw new EmployeeException(EMPLOYEE_WITH_SAME_DATA_FOUND_MSG);
        }
        DepartmentModel departmentModel = departmentService.findDepartmentById(employeeRequestDTO.departmentId());

        DesignationModel designationModel = designationService.findDesignationById(employeeRequestDTO.designationId());

        EmployeeModel employee = new EmployeeModel();
        employee.setFirstName(employeeRequestDTO.firstName());
        employee.setMiddleName(employeeRequestDTO.middleName());
        employee.setLastName(employeeRequestDTO.lastName());
        employee.setEmail(employeeRequestDTO.email());
        employee.setDob(employeeRequestDTO.dob());
        employee.setContactNumber(employeeRequestDTO.contactNumber());
        employee.setHireDate(employeeRequestDTO.hireDate());
        employee.setEpfNumber(employeeRequestDTO.epfNumber());
        employee.setNationalIdNumber(employeeRequestDTO.nationalIdNumber());
        employee.setGender(employeeRequestDTO.gender());
        employee.setMarital(employeeRequestDTO.marital());
        employee.setDepartment(departmentModel);
        employee.setDesignation(designationModel);
        employee.setStatus(StatusEnum.ACTIVE);

        Instant start = Instant.now();
        EmployeeModel savedEmployee = employeeRepository.save(employee);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Employee created successfully for employee id {} in {} ms", employee.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Employee Created Successfully", convertToResponseDTO(savedEmployee));
    }

    public ApiResponseDTO<EmployeeResponseDTO> getEmployeeById(Long id) {
        logger.info("Starting to get employee by id for : {}", id);

        return new ApiResponseDTO<>("Employee Fetched Successfully", convertToResponseDTO(findEmployeeById(id)));
    }

    public EmployeeModel getEmployeeModelById(Long id) {
        logger.info("Starting to get employee model by id for : {}", id);

        return findEmployeeById(id);
    }

    private EmployeeModel findEmployeeById(Long id) {
        logger.info("Starting to find employee for id: {}", id);

        return employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND_MSG + id));
    }

    public ApiResponseDTO<Page<EmployeeResponseDTO>> getAllEmployees(int page, int size) {
        logger.info("Fetching all employees for page {} with size {}.", page, size);

        Pageable pageable = PageRequest.of(page, size);

        Instant start = Instant.now();
        Page<EmployeeModel> employees = employeeRepository.findAll(pageable);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Successfully fetched all {} employees in {} ms.", employees.getTotalElements(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Successfully Fetched All Employees", employees.map(this::convertToResponseDTO));
    }

    public ApiResponseDTO<Page<EmployeeResponseDTO>> getAllActiveEmployees(int page, int size) {
        logger.info("Fetching all active employees for page {} with size {}.", page, size);

        Pageable pageable = PageRequest.of(page, size);

        Instant start = Instant.now();
        Page<EmployeeModel> employees = employeeRepository.findByStatus(StatusEnum.ACTIVE, pageable);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Successfully fetched all active {} employees in {} ms.", employees.getTotalElements(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Successfully Fetched All Active Employees", employees.map(this::convertToResponseDTO));
    }

    @Transactional
    public ApiResponseDTO<EmployeeResponseDTO> updateEmployee(Long id, EmployeeRequestDTO employeeRequestDTO) {
        logger.info("Starting to update employee for : {}", employeeRequestDTO);

        EmployeeModel employee = employeeRepository.findById(id)
                .orElseThrow(() -> new EmployeeNotFoundException(EMPLOYEE_NOT_FOUND_MSG + id));

        DepartmentModel departmentModel = departmentService.findDepartmentById(employeeRequestDTO.departmentId());

        DesignationModel designationModel = designationService.findDesignationById(employeeRequestDTO.designationId());

        employee.setFirstName(employeeRequestDTO.firstName());
        employee.setMiddleName(employeeRequestDTO.middleName());
        employee.setLastName(employeeRequestDTO.lastName());
        employee.setEmail(employeeRequestDTO.email());
        employee.setDob(employeeRequestDTO.dob());
        employee.setContactNumber(employeeRequestDTO.contactNumber());
        employee.setHireDate(employeeRequestDTO.hireDate());
        employee.setEpfNumber(employeeRequestDTO.epfNumber());
        employee.setNationalIdNumber(employeeRequestDTO.nationalIdNumber());
        employee.setGender(employeeRequestDTO.gender());
        employee.setMarital(employeeRequestDTO.marital());
        employee.setDepartment(departmentModel);
        employee.setDesignation(designationModel);

        if (employeeRequestDTO.status() != null) {
            employee.setStatus(StatusEnum.valueOf(String.valueOf(employeeRequestDTO.status())));
        }
        Instant start = Instant.now();
        employeeRepository.save(employee);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Employee updated successfully for employee id {} in {} ms", employee.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Employee Updated Successfully", convertToResponseDTO(employee));
    }

    public ApiResponseDTO<EmployeeResponseDTO> updateEmployeeStatus(Long id, String status) {
        logger.info("Starting to update employee status for : {}", id);

        EmployeeModel employee = findEmployeeById(id);
        employee.setStatus(StatusEnum.valueOf(status));

        Instant start = Instant.now();
        EmployeeModel updatedEmployee = employeeRepository.save(employee);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Employee status updated successfully for Employee id {} in {} ms", updatedEmployee.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Employee Status Updated Successfully",  convertToResponseDTO(updatedEmployee));
    }

    public void deleteEmployee(Long id) {
        logger.info("Starting to delete employee for id: {}", id);

        EmployeeModel employee = findEmployeeById(id);
        employee.setStatus(StatusEnum.INACTIVE);

        Instant start = Instant.now();
        employeeRepository.save(employee);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Employee deleted successfully for employee id {} in {} ms", id, timeElapsed.toMillis());
    }

    public ApiResponseDTO<Page<EmployeeResponseDTO>> filterEmployees(
            EmployeeFilterCriteria criteria,
            int page,
            int size)
    {
        Specification<EmployeeModel> specification = EmployeeSpecification.employeeSpecification(
                criteria.contactNumber(),
                criteria.hireFrom(),
                criteria.hireTo(),
                criteria.status(),
                criteria.departmentId(),
                criteria.designationId(),
                criteria.searchQuery()
        );

        Page<EmployeeModel> employees = employeeRepository.findAll(specification, PageRequest.of(page, size));
        return new ApiResponseDTO<>("Filtering Employee Successfully",  employees.map(this::convertToResponseDTO));
    }

    @Transactional
    public DesignationManagementHistoryResponseDTO changeEmployeeDesignation(DesignationManagementHistoryRequestDTO request) {
        EmployeeModel employee = employeeRepository.findById(request.employeeId())
                .orElseThrow(() -> new RuntimeException("Employee not found"));

        Long previousDesignationId  = employee.getDesignation().getId();
        Long previousDepartmentId  = employee.getDepartment().getId();
        String previousDesignationTitle = employee.getDesignation().getJobTitle();
        String previousDepartmentName = employee.getDepartment().getName();

        DesignationModel newDesignation = designationService.findDesignationById(request.designationId());
        DepartmentModel newDepartment = departmentService.findDepartmentById(request.departmentId());

        if (previousDesignationId.equals(newDesignation.getId()) && previousDepartmentId.equals(newDepartment.getId())) {
            throw new DesignationManagementHistoryException("No changes detected in designation or department.");
        }

        designationManagementHistoryService.logDesignationChange(
                employee,
                previousDesignationId,
                newDesignation.getId(),
                previousDepartmentId,
                newDepartment.getId()
        );

        employee.setDesignation(newDesignation);
        employee.setDepartment(newDepartment);
        employeeRepository.save(employee);

        return new DesignationManagementHistoryResponseDTO(
                employee.getId(),
                employee.getFirstName(),
                employee.getLastName(),
                previousDesignationTitle,
                newDesignation.getJobTitle(),
                previousDepartmentName,
                newDepartment.getName(),
                new Date()
        );
    }

    private EmployeeResponseDTO convertToResponseDTO(EmployeeModel employee) {
        return new EmployeeResponseDTO(
                employee.getId(),
                employee.getFirstName(),
                employee.getMiddleName(),
                employee.getLastName(),
                employee.getEmail(),
                employee.getDob(),
                employee.getContactNumber(),
                employee.getHireDate(),
                employee.getEpfNumber(),
                employee.getNationalIdNumber(),
                employee.getGender(),
                employee.getMarital(),
                employee.getDepartment(),
                employee.getDesignation(),
                employee.getStatus()
        );
    }
}
