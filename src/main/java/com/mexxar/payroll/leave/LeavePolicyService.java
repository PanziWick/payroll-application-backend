package com.mexxar.payroll.leave;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.leave.exception.LeavePolicyNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class LeavePolicyService {

    private final LeavePolicyRepository leavePolicyRepository;

    public LeavePolicyService(LeavePolicyRepository leavePolicyRepository) {
        this.leavePolicyRepository = leavePolicyRepository;
    }

    private static final String LEAVE_POLICY_NOT_FOUND_MSG = "Leave Policy not found with id: ";

    public ApiResponseDTO<LeavePolicyResponseDTO> createLeavePolicy(LeavePolicyRequestDTO leavePolicyRequestDTO) {

        LeavePolicyModel leavePolicyModel = new LeavePolicyModel();
        leavePolicyModel.setName(leavePolicyRequestDTO.name());
        leavePolicyModel.setYear(leavePolicyRequestDTO.year());
        leavePolicyModel.setLeaveType(leavePolicyRequestDTO.leaveType());
        leavePolicyModel.setMaxDays(leavePolicyRequestDTO.maxDays());
        leavePolicyModel.setCarryForwardAllowed(leavePolicyRequestDTO.carryForwardAllowed());

        LeavePolicyModel savedLeavePolicy = leavePolicyRepository.save(leavePolicyModel);

        return new ApiResponseDTO<>("Leave Policy Created Successfully", convertToResponseDTO(savedLeavePolicy));
    }

    public ApiResponseDTO<LeavePolicyResponseDTO> getLeavePolicyById(Long id) {
        LeavePolicyModel leavePolicy = leavePolicyRepository.findById(id)
                .orElseThrow(() -> new LeavePolicyNotFoundException(LEAVE_POLICY_NOT_FOUND_MSG + id));

        return new ApiResponseDTO<>("Successfully Fetched Leave Policy", convertToResponseDTO(leavePolicy));
    }

    public LeavePolicyModel getLeavePolicyModelById(Long id) {
        return leavePolicyRepository.findById(id)
                .orElseThrow(() -> new LeavePolicyNotFoundException(LEAVE_POLICY_NOT_FOUND_MSG + id));
    }

    public ApiResponseDTO<List<LeavePolicyResponseDTO>> getAllLeavePolicies() {
        List<LeavePolicyResponseDTO> leavePolicies = leavePolicyRepository.findAll().stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());

        return new ApiResponseDTO<>("Successfully Fetched All Leave Policies", leavePolicies);
    }

    public ApiResponseDTO<LeavePolicyResponseDTO> updateLeavePolicy(Long id, LeavePolicyRequestDTO leavePolicyRequestDTO) {
        LeavePolicyModel leavePolicy = leavePolicyRepository.findById(id)
                .orElseThrow(() -> new LeavePolicyNotFoundException(LEAVE_POLICY_NOT_FOUND_MSG + id));

        leavePolicy.setName(leavePolicyRequestDTO.name());
        leavePolicy.setYear(leavePolicyRequestDTO.year());
        leavePolicy.setLeaveType(leavePolicyRequestDTO.leaveType());
        leavePolicy.setMaxDays(leavePolicyRequestDTO.maxDays());
        leavePolicy.setCarryForwardAllowed(leavePolicyRequestDTO.carryForwardAllowed());

        LeavePolicyModel updatedPolicy = leavePolicyRepository.save(leavePolicy);

        return new ApiResponseDTO<>("Successfully Updated Leave Policy", convertToResponseDTO(updatedPolicy));
    }

    public ApiResponseDTO<Void> deleteLeavePolicy(Long id) {
        leavePolicyRepository.deleteById(id);

        return new ApiResponseDTO<>("Successfully Deleted Leave Policy", null);
    }

    private LeavePolicyResponseDTO convertToResponseDTO(LeavePolicyModel leavePolicy) {
        return new LeavePolicyResponseDTO(
                leavePolicy.getId(),
                leavePolicy.getName(),
                leavePolicy.getYear(),
                leavePolicy.getLeaveType(),
                leavePolicy.getMaxDays(),
                leavePolicy.isCarryForwardAllowed()
        );
    }
}
