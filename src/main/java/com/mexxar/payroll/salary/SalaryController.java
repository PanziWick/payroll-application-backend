package com.mexxar.payroll.salary;

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

@RestController
@RequestMapping("/api/salaries")
public class SalaryController {

    private final SalaryService salaryService;

    @Autowired
    public SalaryController(SalaryService salaryService) {
        this.salaryService = salaryService;
    }

    private static final Logger logger = LogManager.getLogger(SalaryController.class);

    @Operation(summary = "Create a new salary record", description = "This endpoint creates a new salary record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the salary record"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<SalaryResponseDTO>> createSalary(@Valid @RequestBody SalaryRequestDTO salaryRequestDTO) {
        logger.info("Received request to create a new Salary record");
        ApiResponseDTO<SalaryResponseDTO> createdSalary = salaryService.createSalary(salaryRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdSalary);
    }

    @Operation(summary = "Get salary by ID", description = "This endpoint retrieves a salary record by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the salary record"),
            @ApiResponse(responseCode = "404", description = "Salary record not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<SalaryResponseDTO>> getSalaryById(@PathVariable Long id) {
        logger.info("Received request to get a Salary by ID");
        ApiResponseDTO<SalaryResponseDTO> salary = salaryService.getSalaryById(id);
        return ResponseEntity.ok(salary);
    }

    @Operation(summary = "Get all salary records", description = "This endpoint retrieves all salary records with pagination.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved salary records"),
            @ApiResponse(responseCode = "204", description = "No content, no salary records found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<SalaryResponseDTO>>> getAllSalaries(Pageable pageable) {
        logger.info("Received request to get all Salary records");
        ApiResponseDTO<Page<SalaryResponseDTO>> salaries = salaryService.getAllSalaries(pageable);

        if (salaries.getData().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(salaries);
        }
    }

    @Operation(summary = "Update a salary record", description = "This endpoint updates an existing salary record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the salary record"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Salary record not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<SalaryResponseDTO>> updateSalary(
            @PathVariable Long id,
            @Valid @RequestBody SalaryRequestDTO salaryRequestDTO) {
        logger.info("Received request to update a Salary record");
        ApiResponseDTO<SalaryResponseDTO> updatedSalary = salaryService.updateSalary(id, salaryRequestDTO);
        return ResponseEntity.ok(updatedSalary);
    }

    @Operation(summary = "Delete a salary record", description = "This endpoint deletes a salary record by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the salary record"),
            @ApiResponse(responseCode = "404", description = "Salary record not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteSalary(@PathVariable Long id) {
        logger.info("Received request to delete a Salary record");
        ApiResponseDTO<Void> response = salaryService.deleteSalary(id);
        return ResponseEntity.ok(response);
    }

}
