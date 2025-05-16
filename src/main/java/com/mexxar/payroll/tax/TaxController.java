package com.mexxar.payroll.tax;

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
@RequestMapping("/api/tax")
public class TaxController {

    private final TaxService taxService;

    public TaxController(final TaxService taxService) {
        this.taxService = taxService;
    }

    private static final Logger logger = LogManager.getLogger(TaxController.class);

    @Operation(summary = "Create a new tax record", description = "This endpoint creates a new tax record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the tax record"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<TaxResponseDTO>> createTax(@Valid @RequestBody TaxRequestDTO requestDTO) {
        logger.info("Received request to create a new Tax record");
        ApiResponseDTO<TaxResponseDTO> createdTax = taxService.createTax(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdTax);
    }

    @Operation(summary = "Get tax by ID", description = "This endpoint retrieves a tax record by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the tax record"),
            @ApiResponse(responseCode = "404", description = "Tax record not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<TaxResponseDTO>> getTaxById(@PathVariable Long id) {
        logger.info("Received request to get Tax record by ID");
        ApiResponseDTO<TaxResponseDTO> tax = taxService.getTaxById(id);
        return ResponseEntity.ok(tax);
    }

    @Operation(summary = "Get tax rate by salary range", description = "This endpoint retrieves tax rates based on the salary range.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved tax rates"),
            @ApiResponse(responseCode = "404", description = "No tax rates found for the specified salary range")
    })
    @GetMapping("/rate")
    public ResponseEntity<ApiResponseDTO<List<TaxResponseDTO>>> getTaxRateBySalary(@RequestParam Double salary) {
        logger.info("Received request to get Tax rate by salary range: {}", salary);
        ApiResponseDTO<List<TaxResponseDTO>> taxRate = taxService.getTaxRateBySalaryRange(salary);
        if (taxRate.getData().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(taxRate);
    }

    @Operation(summary = "Get all tax records", description = "This endpoint retrieves all tax records.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved all tax records"),
            @ApiResponse(responseCode = "204", description = "No tax records found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<TaxResponseDTO>>> getAllTaxes() {
        logger.info("Received request to get all Tax records");
        ApiResponseDTO<List<TaxResponseDTO>> taxes = taxService.getAllTaxes();
        if (taxes.getData().isEmpty()) {
            return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
        }
        return ResponseEntity.ok(taxes);
    }

    @Operation(summary = "Update a tax record", description = "This endpoint updates an existing tax record.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the tax record"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Tax record not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<TaxResponseDTO>> updateTax(@PathVariable Long id, @Valid @RequestBody TaxRequestDTO requestDTO) {
        logger.info("Received request to update Tax record with ID: {}", id);
        ApiResponseDTO<TaxResponseDTO> tax = taxService.updateTax(id, requestDTO);
        return ResponseEntity.ok(tax);
    }

    @Operation(summary = "Delete a tax record", description = "This endpoint deletes a tax record by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the tax record"),
            @ApiResponse(responseCode = "404", description = "Tax record not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteTax(@PathVariable Long id) {
        logger.info("Received request to delete Tax record with ID: {}", id);
        ApiResponseDTO<Void> response = taxService.deleteTax(id);
        return ResponseEntity.ok(response);
    }
}
