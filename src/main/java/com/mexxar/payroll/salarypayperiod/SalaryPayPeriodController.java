package com.mexxar.payroll.salarypayperiod;

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
@RequestMapping("/api/salaryPayPeriod")
public class SalaryPayPeriodController {

    private final SalaryPayPeriodService salaryPayPeriodService;

    public SalaryPayPeriodController(SalaryPayPeriodService salaryPayPeriodService) {
        this.salaryPayPeriodService = salaryPayPeriodService;
    }

    private static final Logger logger = LogManager.getLogger(SalaryPayPeriodController.class);

    @Operation(summary = "Create a new salary pay period", description = "This endpoint creates a new salary pay period.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the salary pay period"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<SalaryPayPeriodResponseDTO>> createPayPeriod(@Valid @RequestBody SalaryPayPeriodRequestDTO request) {
        logger.info("Received request to create a new Salary Pay Period");
        ApiResponseDTO<SalaryPayPeriodResponseDTO> createdPayPeriod = salaryPayPeriodService.createPayPeriod(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPayPeriod);
    }

    @Operation(summary = "Get salary pay period by ID", description = "This endpoint retrieves a salary pay period by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the salary pay period"),
            @ApiResponse(responseCode = "404", description = "Salary pay period not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<SalaryPayPeriodResponseDTO>> getPayPeriodById(@PathVariable Long id) {
        logger.info("Received request to get a Salary Pay Period by ID");
        ApiResponseDTO<SalaryPayPeriodResponseDTO> payPeriod = salaryPayPeriodService.getPayPeriodById(id);
        return ResponseEntity.ok(payPeriod);
    }

    @Operation(summary = "Get all salary pay periods", description = "This endpoint retrieves all salary pay periods.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved salary pay periods"),
            @ApiResponse(responseCode = "204", description = "No content, no salary pay periods found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<SalaryPayPeriodResponseDTO>>> getAllPayPeriods() {
        logger.info("Received request to get all Salary Pay Periods");
        ApiResponseDTO<List<SalaryPayPeriodResponseDTO>> payPeriods = salaryPayPeriodService.getAllPayPeriods();

        if (payPeriods.getData().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(payPeriods);
        }
    }

    @Operation(summary = "Update a salary pay period", description = "This endpoint updates an existing salary pay period.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the salary pay period"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Salary pay period not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<SalaryPayPeriodResponseDTO>> updatePayPeriod(@PathVariable Long id, @Valid @RequestBody SalaryPayPeriodRequestDTO request) {
        logger.info("Received request to update a Salary Pay Period");
        ApiResponseDTO<SalaryPayPeriodResponseDTO> updatedPayPeriod = salaryPayPeriodService.updatePayPeriod(id, request);
        return ResponseEntity.ok(updatedPayPeriod);
    }

    @Operation(summary = "Delete a salary pay period", description = "This endpoint deletes a salary pay period by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the salary pay period"),
            @ApiResponse(responseCode = "404", description = "Salary pay period not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deletePayPeriod(@PathVariable Long id) {
        logger.info("Received request to delete a Salary Pay Period");
        ApiResponseDTO<Void> response = salaryPayPeriodService.deletePayPeriod(id);
        return ResponseEntity.ok(response);
    }
}
