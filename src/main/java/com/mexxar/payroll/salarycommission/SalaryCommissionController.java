package com.mexxar.payroll.salarycommission;

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
@RequestMapping("/api/salaryCommission")
public class SalaryCommissionController {

    private final SalaryCommissionService salaryCommissionService;

    public SalaryCommissionController(SalaryCommissionService salaryCommissionService) {
        this.salaryCommissionService = salaryCommissionService;
    }

    private static final Logger logger = LogManager.getLogger(SalaryCommissionController.class);

    @Operation(summary = "Create a new salary commission", description = "This endpoint creates a new salary commission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the salary commission"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<SalaryCommissionResponseDTO>> createSalaryCommission(@Valid @RequestBody SalaryCommissionRequestDTO requestDTO) {
        logger.info("Received request to create salary commission");
        ApiResponseDTO<SalaryCommissionResponseDTO> responseDTO = salaryCommissionService.createSalaryCommission(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
    }

    @Operation(summary = "Get Salary Commission by ID", description = "This endpoint returns a salary commission by given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the salary commission"),
            @ApiResponse(responseCode = "404", description = "Salary commission not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<SalaryCommissionResponseDTO>> getSalaryCommissionById(@PathVariable Long id) {
        logger.info("Received request to get salary commission by id");
        ApiResponseDTO<SalaryCommissionResponseDTO> responseDTO = salaryCommissionService.getSalaryCommissionById(id);
        return ResponseEntity.status(HttpStatus.OK).body(responseDTO);
    }

    @Operation(summary = "Get all salary commissions", description = "This endpoint returns a list of all salary commissions")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved salary commission list"),
            @ApiResponse(responseCode = "204", description = "No content, no salary commissions found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<SalaryCommissionResponseDTO>>> getAllSalaryCommissions() {
        logger.info("Received request to get all salary commissions");
        ApiResponseDTO<List<SalaryCommissionResponseDTO>> responseDTOList = salaryCommissionService.getAllSalaryCommissions();

        if (responseDTOList.getData().isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(responseDTOList);
        }
    }

    @Operation(summary = "Update a salary commission", description = "This endpoint updates an existing salary commission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the salary commission"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Salary commission not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<SalaryCommissionResponseDTO>> updateSalaryCommission(
            @PathVariable Long id,
            @Valid @RequestBody SalaryCommissionRequestDTO requestDTO) {
        logger.info("Received request to update salary commission by id");
        ApiResponseDTO<SalaryCommissionResponseDTO> updatedResponseDTO = salaryCommissionService.updateSalaryCommission(id, requestDTO);
        return ResponseEntity.ok(updatedResponseDTO);
    }

    @Operation(summary = "Delete a salary commission", description = "This endpoint deletes a salary commission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the salary commission"),
            @ApiResponse(responseCode = "404", description = "Salary commission not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteSalaryCommission(@PathVariable Long id) {
        logger.info("Received request to delete salary commission by id");
        ApiResponseDTO<Void> response = salaryCommissionService.deleteSalaryCommission(id);
        return ResponseEntity.ok(response);
    }
}
