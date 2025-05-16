package com.mexxar.payroll.salaryallowance;

import com.mexxar.payroll.common.ApiResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/salaryAllowance")
public class SalaryAllowanceController {

    private final SalaryAllowanceService salaryAllowanceService;

    public SalaryAllowanceController(SalaryAllowanceService salaryAllowanceService) {
        this.salaryAllowanceService = salaryAllowanceService;
    }

    private static final Logger logger = LogManager.getLogger(SalaryAllowanceController.class);

    @Operation(summary = "Create a new salary allowance", description = "This endpoint creates a new salary allowance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the salary allowance"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<SalaryAllowanceResponseDTO>> createSalaryAllowance(@Valid @RequestBody SalaryAllowanceRequestDTO requestDTO) {
        logger.info("Received request to create salary allowance");
        ApiResponseDTO<SalaryAllowanceResponseDTO> responseDTO = salaryAllowanceService.createSalaryAllowance(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @Operation(summary = "Get Salary Allowance by ID", description = "This endpoint returns a salary allowance by given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the salary allowance"),
            @ApiResponse(responseCode = "404", description = "Salary allowance not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<SalaryAllowanceResponseDTO>> getSalaryAllowanceById(@PathVariable Long id) {
        logger.info("Received request to get salary allowance by id");
        ApiResponseDTO<SalaryAllowanceResponseDTO> responseDTO = salaryAllowanceService.getSalaryAllowanceById(id);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @Operation(summary = "Get all salary allowances", description = "This endpoint returns a list of all salary allowances")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved salary allowance list"),
            @ApiResponse(responseCode = "204", description = "No content, no salary allowances found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<SalaryAllowanceResponseDTO>>> getAllSalaryAllowances() {
        logger.info("Received request to get all salary allowances");
        ApiResponseDTO<List<SalaryAllowanceResponseDTO>> responseDTOList = salaryAllowanceService.getAllSalaryAllowances();

        if (responseDTOList.getData().isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(responseDTOList);
        }
    }

    @Operation(summary = "Update a salary allowance", description = "This endpoint updates an existing salary allowance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the salary allowance"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Salary allowance not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<SalaryAllowanceResponseDTO>> updateSalaryAllowance(
            @PathVariable Long id,
            @Valid @RequestBody SalaryAllowanceRequestDTO requestDTO) {
        logger.info("Received request to update salary allowance by id");
        ApiResponseDTO<SalaryAllowanceResponseDTO> updatedResponseDTO = salaryAllowanceService.updateSalaryAllowance(id, requestDTO);
        return ResponseEntity.ok(updatedResponseDTO);
    }

    @Operation(summary = "Delete a salary allowance", description = "This endpoint deletes a salary allowance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the salary allowance"),
            @ApiResponse(responseCode = "404", description = "Salary allowance not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteSalaryAllowance(@PathVariable Long id) {
        logger.info("Received request to delete salary allowance by id");
        ApiResponseDTO<Void> response = salaryAllowanceService.deleteSalaryAllowance(id);
        return ResponseEntity.ok(response);
    }
}
