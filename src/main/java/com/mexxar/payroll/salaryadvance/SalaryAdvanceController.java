package com.mexxar.payroll.salaryadvance;

import com.mexxar.payroll.common.ApiResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/salary-advances")
public class SalaryAdvanceController {

    private final SalaryAdvanceService salaryAdvanceService;

    @Autowired
    public SalaryAdvanceController(SalaryAdvanceService salaryAdvanceService) {
        this.salaryAdvanceService = salaryAdvanceService;
    }

    private static final Logger logger = LogManager.getLogger(SalaryAdvanceController.class);

    @Operation(summary = "Create a new salary advance record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Salary advance record created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid salary advance data provided")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<SalaryAdvanceResponseDTO>> createSalaryAdvance(@Valid @RequestBody SalaryAdvanceRequestDTO salaryAdvanceRequestDTO) {
        logger.info("Received request to create a new Salary Advance record");
        ApiResponseDTO<SalaryAdvanceResponseDTO> createdSalaryAdvance = salaryAdvanceService.createSalaryAdvance(salaryAdvanceRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSalaryAdvance);
    }

    @Operation(summary = "Get a salary advance by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved salary advance"),
            @ApiResponse(responseCode = "404", description = "Salary advance record not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<SalaryAdvanceResponseDTO>> getSalaryAdvanceById(@PathVariable Long id) {
        logger.info("Received request to get Salary Advance by ID: {}", id);
        ApiResponseDTO<SalaryAdvanceResponseDTO> salaryAdvance = salaryAdvanceService.getSalaryAdvanceById(id);
        return ResponseEntity.ok(salaryAdvance);
    }

    @Operation(summary = "Get all salary advances by Employee ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved salary advances by Employee ID"),
            @ApiResponse(responseCode = "404", description = "Salary advance records not found for Employee ID")
    })
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponseDTO<List<SalaryAdvanceResponseDTO>>> getSalaryAdvancesByEmployeeId(@PathVariable Long employeeId) {
        logger.info("Received request to get Salary Advances by Employee ID: {}", employeeId);
        ApiResponseDTO<List<SalaryAdvanceResponseDTO>> salaryAdvances = salaryAdvanceService.getSalaryAdvancesByEmployeeId(employeeId);
        return salaryAdvances.getData().isEmpty() ? ResponseEntity.status(HttpStatus.NOT_FOUND).build() : ResponseEntity.ok(salaryAdvances);
    }

    @Operation(summary = "Get all salary advance records with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved salary advance records"),
            @ApiResponse(responseCode = "204", description = "No salary advance records found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<SalaryAdvanceResponseDTO>>> getAllSalaryAdvances(Pageable pageable) {
        logger.info("Received request to get all Salary Advance records");
        ApiResponseDTO<Page<SalaryAdvanceResponseDTO>> salaryAdvances = salaryAdvanceService.getAllSalaryAdvances(pageable);
        return salaryAdvances.getData().isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(salaryAdvances);
    }

    @Operation(summary = "Update a salary advance record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Salary advance record updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid salary advance data provided"),
            @ApiResponse(responseCode = "404", description = "Salary advance record not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<SalaryAdvanceResponseDTO>> updateSalaryAdvance(
            @PathVariable Long id,
            @Valid @RequestBody SalaryAdvanceRequestDTO salaryAdvanceRequestDTO) {
        logger.info("Received request to update Salary Advance record with ID: {}", id);
        ApiResponseDTO<SalaryAdvanceResponseDTO> updatedSalaryAdvance = salaryAdvanceService.updateSalaryAdvance(id, salaryAdvanceRequestDTO);
        return ResponseEntity.ok(updatedSalaryAdvance);
    }

    @Operation(summary = "Delete a salary advance record by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Salary advance record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Salary advance record not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteSalaryAdvance(@PathVariable Long id) {
        logger.info("Received request to delete Salary Advance record with ID: {}", id);
        ApiResponseDTO<Void> response = salaryAdvanceService.deleteSalaryAdvance(id);
        return ResponseEntity.ok(response);
    }
}
