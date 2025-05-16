package com.mexxar.payroll.allowancetype;

import com.mexxar.payroll.allowancetype.exception.AllowanceTypeNotFoundException;
import com.mexxar.payroll.common.ApiResponseDTO;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class AllowanceTypeService {

    private final AllowanceTypeRepository allowanceTypeRepository;

    public AllowanceTypeService(AllowanceTypeRepository allowanceTypeRepository) {
        this.allowanceTypeRepository = allowanceTypeRepository;
    }

    private static final Logger logger = LogManager.getLogger(AllowanceTypeService.class);

    private static final String ALLOWANCE_NOT_FOUND_MSG = "Allowance not found with id: ";

    @Transactional
    public ApiResponseDTO<AllowanceTypeResponseDTO> createAllowanceType(AllowanceTypeRequestDTO allowanceTypeRequestDTO) {
        logger.info("Starting to create allowance for: {}", allowanceTypeRequestDTO);

        AllowanceTypeModel allowance = new AllowanceTypeModel();
        allowance.setName(allowanceTypeRequestDTO.name());
        allowance.setIsFixed(allowanceTypeRequestDTO.isFixed());
        allowance.setIsLiableToTax(allowanceTypeRequestDTO.isLiableToTax());
        allowance.setDescription(allowanceTypeRequestDTO.description());

        Instant startTime = Instant.now();
        allowanceTypeRepository.save(allowance);
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Allowance created successfully for allowance id {} in {} ms", allowance.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Allowance Created Successfully", convertToResponseDTO(allowance));
    }

    public ApiResponseDTO<AllowanceTypeResponseDTO> getAllowanceTypeById(Long id) {
        logger.info("Starting to get allowance by id: {}", id);

        Instant startTime = Instant.now();
        AllowanceTypeModel allowance = allowanceTypeRepository.findById(id)
                .orElseThrow(() -> new AllowanceTypeNotFoundException(ALLOWANCE_NOT_FOUND_MSG + id));
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Allowance fetched successfully for allowance id {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Allowance Fetched Successfully",  convertToResponseDTO(allowance));
    }

    public Optional<AllowanceTypeModel> getAllowanceTypeModelById(Long id) {
        return allowanceTypeRepository.findById(id);
    }

    public ApiResponseDTO<List<AllowanceTypeResponseDTO>> getAllAllowanceTypes() {
        logger.info("Starting to get all allowances");

        Instant startTime = Instant.now();
        List<AllowanceTypeModel> allowances = allowanceTypeRepository.findAll();
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Successfully fetched {} allowances in {} ms", allowances.size(), timeElapsed.toMillis());

        List<AllowanceTypeResponseDTO> responseDTOs = allowances.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return new ApiResponseDTO<>("Successfully Fetched All Allowances", responseDTOs);
    }

    @Transactional
    public ApiResponseDTO<AllowanceTypeResponseDTO> updateAllowanceType(Long id, AllowanceTypeRequestDTO allowanceTypeRequestDTO) {
        logger.info("Starting to update allowance by id: {}", id);

        AllowanceTypeModel allowance = allowanceTypeRepository.findById(id)
                .orElseThrow(() -> new AllowanceTypeNotFoundException(ALLOWANCE_NOT_FOUND_MSG + id));

        allowance.setName(allowanceTypeRequestDTO.name());
        allowance.setIsFixed(allowanceTypeRequestDTO.isFixed());
        allowance.setIsLiableToTax(allowanceTypeRequestDTO.isLiableToTax());
        allowance.setDescription(allowanceTypeRequestDTO.description());

        Instant startTime = Instant.now();
        allowanceTypeRepository.save(allowance);
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Allowance updated successfully for allowance id {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Allowance Updated Successfully",  convertToResponseDTO(allowance));
    }

    public ApiResponseDTO<Void> deleteAllowanceType(Long id) {
        logger.info("Starting to delete allowance by id: {}", id);

        AllowanceTypeModel allowanceType = allowanceTypeRepository.findById(id)
                .orElseThrow(() -> new AllowanceTypeNotFoundException(ALLOWANCE_NOT_FOUND_MSG + id));
        Instant startTime = Instant.now();
        allowanceTypeRepository.delete(allowanceType);
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Allowance deleted successfully for allowance id {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Allowance Deleted Successfully", null);
    }

    public AllowanceTypeResponseDTO convertToResponseDTO(AllowanceTypeModel allowance) {
        return new AllowanceTypeResponseDTO(
                allowance.getId(),
                allowance.getName(),
                allowance.getIsFixed(),
                allowance.getIsLiableToTax(),
                allowance.getDescription()
        );
    }
}
