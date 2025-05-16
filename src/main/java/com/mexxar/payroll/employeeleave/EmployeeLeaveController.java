package com.mexxar.payroll.employeeleave;

import com.mexxar.payroll.common.ApiResponseDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/employeeLeave")
public class EmployeeLeaveController {

    private final EmployeeLeaveService employeeLeaveService;

    public EmployeeLeaveController(EmployeeLeaveService employeeLeaveService) {
        this.employeeLeaveService = employeeLeaveService;
    }

    @PostMapping
    public ResponseEntity<ApiResponseDTO<EmployeeLeaveResponseDTO>> createEmployeeLeave(@RequestBody EmployeeLeaveRequestDTO requestDTO) {
        ApiResponseDTO<EmployeeLeaveResponseDTO> response = employeeLeaveService.createEmployeeLeave(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EmployeeLeaveResponseDTO>> getEmployeeLeaveById(@PathVariable Long id) {
        ApiResponseDTO<EmployeeLeaveResponseDTO> response = employeeLeaveService.getEmployeeLeaveById(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<EmployeeLeaveResponseDTO>>> getAllEmployeeLeaves() {
        ApiResponseDTO<List<EmployeeLeaveResponseDTO>> response = employeeLeaveService.getAllEmployeeLeaves();
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EmployeeLeaveResponseDTO>> updateEmployeeLeave(
            @PathVariable Long id,
            @RequestBody EmployeeLeaveRequestDTO requestDTO) {
        ApiResponseDTO<EmployeeLeaveResponseDTO> response = employeeLeaveService.updateEmployeeLeave(id, requestDTO);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/{id}/status")
    public ApiResponseDTO<EmployeeLeaveResponseDTO> updateEmployeeLeaveStatus(
            @PathVariable Long id,
            @RequestParam EmployeeLeaveEnum status) {
        return employeeLeaveService.updateEmployeeLeaveStatus(id, status);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteEmployeeLeave(@PathVariable Long id) {
        ApiResponseDTO<Void> response = employeeLeaveService.deleteEmployeeLeave(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/getRemainingLeaveCount")
    public ResponseEntity<ApiResponseDTO<List<EmployeeRemainingLeaveDTO>>> getRemainingLeaveCount(
            @RequestParam Long employeeId,
            @RequestParam Long year) {
        ApiResponseDTO<List<EmployeeRemainingLeaveDTO>> response = employeeLeaveService.getRemainingLeaveCount(employeeId, year);
        return ResponseEntity.ok(response);
    }
}
