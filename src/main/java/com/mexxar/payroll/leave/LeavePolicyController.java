package com.mexxar.payroll.leave;

import com.mexxar.payroll.common.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/leavePolicy")
public class LeavePolicyController {

    private final LeavePolicyService leavePolicyService;

    public LeavePolicyController(LeavePolicyService leavePolicyService) {
        this.leavePolicyService = leavePolicyService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<LeavePolicyResponseDTO>> createLeavePolicy(@RequestBody LeavePolicyRequestDTO leavePolicyRequestDTO) {
        ApiResponseDTO<LeavePolicyResponseDTO> leavePolicyResponseDTO = leavePolicyService.createLeavePolicy(leavePolicyRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(leavePolicyResponseDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<LeavePolicyResponseDTO>> getLeavePolicyById(@PathVariable Long id) {
        ApiResponseDTO<LeavePolicyResponseDTO> response = leavePolicyService.getLeavePolicyById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<LeavePolicyResponseDTO>>> getAllLeavePolicies() {
        ApiResponseDTO<List<LeavePolicyResponseDTO>> response = leavePolicyService.getAllLeavePolicies();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<LeavePolicyResponseDTO>> updateLeavePolicy(
            @PathVariable Long id,
            @RequestBody LeavePolicyRequestDTO requestDTO) {
        ApiResponseDTO<LeavePolicyResponseDTO> response = leavePolicyService.updateLeavePolicy(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteLeavePolicy(@PathVariable Long id) {
        ApiResponseDTO<Void> response = leavePolicyService.deleteLeavePolicy(id);
        return ResponseEntity.ok(response);
    }
}
