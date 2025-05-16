package com.mexxar.payroll.commissiontype;

import com.mexxar.payroll.commissiontype.exception.CommissionTypeNotFoundException;
import com.mexxar.payroll.common.ApiResponseDTO;
import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class CommissionTypeService {

    private final CommissionTypeRepository commissionTypeRepository;

    private static final Logger logger = LogManager.getLogger(CommissionTypeService.class);

    private static final String COMMISSION_NOT_FOUND_MSG = "Commission not found with id: ";

    @Transactional
    public ApiResponseDTO<CommissionTypeResponseDTO> createCommissionType(CommissionTypeRequestDTO commissionTypeRequestDTO) {
        logger.info("Starting to create commission for : {}", commissionTypeRequestDTO);

        CommissionTypeModel commission = new CommissionTypeModel();
        commission.setName(commissionTypeRequestDTO.name());
        commission.setIsLiableToTax(commissionTypeRequestDTO.isLiableToTax());
        commission.setDescription(commissionTypeRequestDTO.description());

        Instant startTime = Instant.now();
        commissionTypeRepository.save(commission);
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Commission created successfully for commission id {} in {} ms", commission.getId(), timeElapsed.toMillis());

        return new ApiResponseDTO<>("Commission Created Successfully", convertToResponseDTO(commission));
    }

    public ApiResponseDTO<CommissionTypeResponseDTO> getCommissionTypeById(Long id) {
        logger.info("Starting to get commission for id : {}", id);

        Instant startTime = Instant.now();
        CommissionTypeModel commission = commissionTypeRepository.findById(id)
                .orElseThrow(() -> new CommissionTypeNotFoundException(COMMISSION_NOT_FOUND_MSG + id));
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Commission fetched successfully for commission id {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Commission Fetched Successfully", convertToResponseDTO(commission));
    }

    public Optional<CommissionTypeModel> getCommissionTypeModelById(Long id) {
        return commissionTypeRepository.findById(id);
    }

    public ApiResponseDTO<List<CommissionTypeResponseDTO>> getAllCommissionTypes() {
        logger.info("Starting to get all commissions");

        Instant startTime = Instant.now();
        List<CommissionTypeModel> commissions = commissionTypeRepository.findAll();
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Successfully fetched {} commissions in {} ms", commissions.size(), timeElapsed.toMillis());

        List<CommissionTypeResponseDTO> responseDTOs = commissions.stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return new ApiResponseDTO<>("Successfully Fetched All Commissions", responseDTOs);
    }

    @Transactional
    public ApiResponseDTO<CommissionTypeResponseDTO> updateCommissionType(Long id, CommissionTypeRequestDTO commissionTypeRequestDTO) {
        logger.info("Starting to update commission for id : {}", id);

        CommissionTypeModel commission = commissionTypeRepository.findById(id)
                .orElseThrow(() -> new CommissionTypeNotFoundException(COMMISSION_NOT_FOUND_MSG + id));
        commission.setName(commissionTypeRequestDTO.name());
        commission.setIsLiableToTax(commissionTypeRequestDTO.isLiableToTax());
        commission.setDescription(commissionTypeRequestDTO.description());

        Instant startTime = Instant.now();
        commissionTypeRepository.save(commission);
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Commission updated successfully for id {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Commission Updated Successfully", convertToResponseDTO(commission));
    }

    public ApiResponseDTO<Void> deleteCommissionType(Long id) {
        logger.info("Starting to delete commission for id : {}", id);

        CommissionTypeModel commissionType = commissionTypeRepository.findById(id)
                .orElseThrow(() -> new CommissionTypeNotFoundException(COMMISSION_NOT_FOUND_MSG + id));
        Instant startTime = Instant.now();
        commissionTypeRepository.delete(commissionType);
        Instant endTime = Instant.now();
        Duration timeElapsed = Duration.between(startTime, endTime);
        logger.info("Commission deleted successfully for commission id {} in {} ms", id, timeElapsed.toMillis());

        return new ApiResponseDTO<>("Commission Deleted Successfully", null);
    }

    private CommissionTypeResponseDTO convertToResponseDTO(CommissionTypeModel commission) {
        return new CommissionTypeResponseDTO(
                commission.getId(),
                commission.getName(),
                commission.getIsLiableToTax(),
                commission.getDescription()
        );
    }
}
