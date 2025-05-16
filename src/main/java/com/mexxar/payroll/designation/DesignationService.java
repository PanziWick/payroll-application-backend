package com.mexxar.payroll.designation;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.common.enums.StatusEnum;
import com.mexxar.payroll.designation.exception.DesignationException;
import com.mexxar.payroll.designation.exception.DesignationNotFoundException;
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
public class DesignationService {

    private final DesignationRepository designationRepository;

    public DesignationService(DesignationRepository designationRepository) {
        this.designationRepository = designationRepository;
    }

    private static final Logger logger = LogManager.getLogger(DesignationService.class);

    private static final String DESIGNATION_NOT_FOUND_MSG = "Designation not found with id: ";
    private static final String DESIGNATION_WITH_SAME_DATA_FOUND_MSG = "Designation with the same job title and job description already exists.";

    @Transactional
    public ApiResponseDTO<DesignationResponseDTO> createDesignation(DesignationRequestDTO designationRequestDTO) {
        if (designationRequestDTO == null) {
            throw new IllegalArgumentException("Request cannot be null");
        }
        logger.debug("Starting to create designation for : {}", designationRequestDTO);

        boolean exists = designationRepository.findByJobTitleAndJobDescription(
                designationRequestDTO.jobTitle(),
                designationRequestDTO.jobDescription()).isPresent();
        if (exists) {
            throw new DesignationException(DESIGNATION_WITH_SAME_DATA_FOUND_MSG);
        }
        DesignationModel designation = new DesignationModel();
        designation.setJobTitle(designationRequestDTO.jobTitle());
        designation.setJobDescription(designationRequestDTO.jobDescription());
        designation.setStatus(designationRequestDTO.status());

        Instant start = Instant.now();
        DesignationModel savedDesignation = designationRepository.save(designation);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Designation created successfully for designation id {} in {} ms", designation.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Designation Created Successfully", convertToResponseDTO(savedDesignation));
    }

    public ApiResponseDTO<DesignationResponseDTO> getDesignationById(Long id) {
        logger.info("Starting to get designation by ID for : {}", id);

        Instant start = Instant.now();
        DesignationModel designation = designationRepository.findById(id)
                .orElseThrow(() -> new DesignationNotFoundException(DESIGNATION_NOT_FOUND_MSG + id));
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Designation fetched successfully for designation id {} in {} ms", designation.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Designation Fetched Successfully", convertToResponseDTO(designation));
    }

    public DesignationModel findDesignationById(Long id) {
        logger.info("Starting to find designation for id: {}", id);

        return designationRepository.findById(id)
                .orElseThrow(() -> new DesignationNotFoundException(DESIGNATION_NOT_FOUND_MSG + id));
    }

    public ApiResponseDTO<Page<DesignationResponseDTO>> getAllDesignations(int page, int size) {
        logger.info("Fetching all designation for page {} with size {}.", page, size);

        Instant start = Instant.now();
        Pageable pageable = PageRequest.of(page, size);
        Page<DesignationModel> designations = designationRepository.findAll(pageable);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Successfully fetched all {} designations in {} ms.", designations.getTotalElements(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Successfully Fetched All Designations", designations.map(this::convertToResponseDTO));
    }

    public ApiResponseDTO<Page<DesignationResponseDTO>> getAllActiveDesignations(int page, int size) {
        logger.info("Fetching all active designation for page {} with size {}.", page, size);

        Pageable pageable = PageRequest.of(page, size);
        Instant start = Instant.now();
        Page<DesignationModel> designations = designationRepository.findByStatus(StatusEnum.ACTIVE, pageable);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Successfully fetched all active {} designations in {} ms.", designations.getTotalElements(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Successfully Fetched All Active Designations", designations.map(this::convertToResponseDTO));
    }

    @Transactional
    public ApiResponseDTO<DesignationResponseDTO> updateDesignation(Long id, DesignationRequestDTO designationDetails) {
        if (designationDetails == null) {
            throw new IllegalArgumentException("Designation details cannot be null");
        }
        logger.info("Starting to update designation for : {}", designationDetails);

        DesignationModel designation = designationRepository.findById(id)
                .orElseThrow(() -> new DesignationNotFoundException(DESIGNATION_NOT_FOUND_MSG + id));

        designation.setJobTitle(designationDetails.jobTitle());
        designation.setJobDescription(designationDetails.jobDescription());
        designation.setStatus(designationDetails.status());
        Instant start = Instant.now();
        designationRepository.save(designation);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Designation updated successfully for designation id {} in {} ms", designation.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Designation Updated Successfully", convertToResponseDTO(designation));
    }

    public ApiResponseDTO<DesignationResponseDTO> updateDesignationStatus(Long id, String status) {
        logger.info("Starting to update designation status for : {}", id);

        DesignationModel designation = designationRepository.findById(id)
                .orElseThrow(() -> new DesignationNotFoundException(DESIGNATION_NOT_FOUND_MSG + id));

        designation.setStatus(StatusEnum.valueOf(status));

        Instant start = Instant.now();
        DesignationModel updatedDesignation = designationRepository.save(designation);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Designation status updated successfully for designation id {} in {} ms", updatedDesignation.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Designation Status Updated Successfully", convertToResponseDTO(updatedDesignation));
    }

    public void deleteDesignation(Long id) {
        logger.info("Starting to delete designation for id: {}", id);

        DesignationModel designation = designationRepository.findById(id)
                .orElseThrow(() -> new DesignationNotFoundException(DESIGNATION_NOT_FOUND_MSG + id));
        designation.setStatus(StatusEnum.INACTIVE);
        Instant start = Instant.now();
        designationRepository.save(designation);
        Instant end = Instant.now();
        Duration timeElapsed = Duration.between(start, end);
        logger.info("Designation deleted successfully for designation id {} in {} ms", id, timeElapsed.toMillis());
    }

    private DesignationResponseDTO convertToResponseDTO(DesignationModel designation) {
        return new DesignationResponseDTO(
                designation.getId(),
                designation.getJobTitle(),
                designation.getJobDescription(),
                designation.getStatus()
        );
    }
}
