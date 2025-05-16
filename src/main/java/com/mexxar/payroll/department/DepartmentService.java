package com.mexxar.payroll.department;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.common.enums.StatusEnum;
import com.mexxar.payroll.department.exception.DepartmentException;
import com.mexxar.payroll.department.exception.DepartmentNotFoundException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;

@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;

    public DepartmentService(DepartmentRepository departmentRepository) {
        this.departmentRepository = departmentRepository;
    }

    private static final Logger logger = LogManager.getLogger(DepartmentService.class);

    private static final String DEPARTMENT_NOT_FOUND_MSG = "Department not found with id: ";
    private static final String DEPARTMENT_ALREADY_EXIST_MSG = "Department already exists";

    @Transactional
    public ApiResponseDTO<DepartmentResponseDTO> createDepartment(DepartmentRequestDTO departmentRequestDTO) {
        if (departmentRequestDTO == null) {
            logger.error("Department creation failed due to null request");
            throw new NullPointerException("Department request must not be null");
        }
        logger.debug("Starting to create department for : {}", departmentRequestDTO);

        if (departmentRepository.findByName(departmentRequestDTO.name()).isPresent()) {
            throw new DepartmentException(DEPARTMENT_ALREADY_EXIST_MSG);
        }
        DepartmentModel department = new DepartmentModel();
        department.setName(departmentRequestDTO.name());
        department.setStatus(departmentRequestDTO.status());

        Instant start = Instant.now();
        DepartmentModel savedDepartment = departmentRepository.save(department);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Department created successfully for department id {} in {} ms", department.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Department Created Successfully", convertToResponseDTO(savedDepartment));
    }

    public ApiResponseDTO<DepartmentResponseDTO> getDepartmentById(Long id) {
        logger.info("Starting to get department by ID for : {}", id);

        Instant start = Instant.now();
        DepartmentModel department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(DEPARTMENT_NOT_FOUND_MSG + id));
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Department fetched successfully for department id {} in {} ms", department.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Department Fetched Successfully", convertToResponseDTO(department));
    }

    public DepartmentModel findDepartmentById(Long id) {
        logger.info("Starting to find department for ID: {}", id);

        return departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentException(DEPARTMENT_NOT_FOUND_MSG + id));
    }

    public ApiResponseDTO<Page<DepartmentResponseDTO>> getAllDepartments(int page, int size) {
        logger.info("Fetching all departments for page {} with size {}.", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Instant start = Instant.now();
        Page<DepartmentModel> departments = departmentRepository.findAll(pageable);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Successfully fetched all {} departments in {} ms.", departments.getTotalElements(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Successfully Fetched All Departments", departments.map(this::convertToResponseDTO));
    }

    public ApiResponseDTO<Page<DepartmentResponseDTO>> getAllActiveDepartments(int page, int size) {
        logger.info("Fetching all active departments for page {} with size {}.", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Instant start = Instant.now();
        Page<DepartmentModel> departments = departmentRepository.findByStatus(StatusEnum.ACTIVE, pageable);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Successfully fetched all active {} departments in {} ms.", departments.getTotalElements(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Successfully Fetched All Active Departments", departments.map(this::convertToResponseDTO));
    }

    @Transactional
    public ApiResponseDTO<DepartmentResponseDTO> updateDepartment(Long id, DepartmentRequestDTO departmentRequestDTO) {
        if (departmentRequestDTO == null) {
            logger.error("Department update failed due to null request");
            throw new NullPointerException("Department request must not be null");
        }
        logger.info("Starting to update department for : {}", departmentRequestDTO);

        DepartmentModel department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(DEPARTMENT_NOT_FOUND_MSG + id));

        department.setName(departmentRequestDTO.name());
        department.setStatus(departmentRequestDTO.status());

        Instant start = Instant.now();
        departmentRepository.save(department);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Department updated successfully for department id {} in {} ms", department.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Department Updated Successfully", convertToResponseDTO(department));
    }

    public ApiResponseDTO<DepartmentResponseDTO> updateDepartmentStatus(Long id, String status) {
        logger.info("Starting to update department status for : {}", id);

        DepartmentModel department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(DEPARTMENT_NOT_FOUND_MSG + id));
        department.setStatus(StatusEnum.valueOf(status));

        Instant start = Instant.now();
        DepartmentModel updatedDepartment = departmentRepository.save(department);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Department status updated successfully for department id {} in {} ms", department.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Department Status Updated Successfully", convertToResponseDTO(updatedDepartment));
    }

    public void deleteDepartment(Long id) {
        logger.info("Starting to delete department for id: {}", id);

        DepartmentModel department = departmentRepository.findById(id)
                .orElseThrow(() -> new DepartmentNotFoundException(DEPARTMENT_NOT_FOUND_MSG + id));
        department.setStatus(StatusEnum.INACTIVE);
        Instant start = Instant.now();
        departmentRepository.save(department);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Department deleted successfully for department id {} in {} ms", id, timeElapsed.toMillis());
    }

    private DepartmentResponseDTO convertToResponseDTO(DepartmentModel department) {
        return new DepartmentResponseDTO(
                department.getId(),
                department.getName(),
                department.getStatus()
        );
    }
}
