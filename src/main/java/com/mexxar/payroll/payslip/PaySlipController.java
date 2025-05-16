package com.mexxar.payroll.payslip;

import com.mexxar.payroll.common.ApiResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/payslips")
public class PaySlipController {

    private final PaySlipService paySlipService;

    public PaySlipController(PaySlipService paySlipService) {
        this.paySlipService = paySlipService;
    }

    private static final Logger logger = LoggerFactory.getLogger(PaySlipController.class);

    @Operation(summary = "Create a new payslip", description = "This endpoint creates a new payslip.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the payslip"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<PaySlipResponseDTO>> createPaySlip(@Valid @RequestBody PaySlipRequestDTO requestDTO) {
        logger.info("Received request to create a PaySlip");
        ApiResponseDTO<PaySlipResponseDTO> createdPaySlip = paySlipService.createPaySlip(requestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPaySlip);
    }

    @Operation(summary = "Get payslip by ID", description = "This endpoint returns a payslip by its given ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the payslip"),
            @ApiResponse(responseCode = "404", description = "Payslip not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PaySlipResponseDTO>> getPaySlipById(@PathVariable Long id) {
        logger.info("Received request to get a PaySlip by ID");
        ApiResponseDTO<PaySlipResponseDTO> paySlip = paySlipService.getPaySlipById(id);
        return ResponseEntity.ok(paySlip);
    }

    @Operation(summary = "Get all payslips", description = "This endpoint returns a list of all payslips.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved payslip list"),
            @ApiResponse(responseCode = "204", description = "No content, no payslip found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<PaySlipResponseDTO>>> getAllPaySlips() {
        logger.info("Received request to get all PaySlips");
        ApiResponseDTO<List<PaySlipResponseDTO>> allPaySlips = paySlipService.getAllPaySlips();
        if (allPaySlips.getData().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(allPaySlips);
        }
    }

    @Operation(summary = "Delete a payslip", description = "This endpoint deletes a payslip by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Successfully deleted the payslip"),
            @ApiResponse(responseCode = "404", description = "Payslip not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deletePaySlip(@PathVariable Long id) {
        logger.info("Received request to delete a PaySlip");
        ApiResponseDTO<Void> response = paySlipService.deletePaySlip(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponseDTO<Page<PaySlipResponseDTO>>> filterPaySlips(
            @ModelAttribute PaySlipFilterCriteria filterCriteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ApiResponseDTO<Page<PaySlipResponseDTO>> filteredPaySlips = paySlipService.filterPaySlips(
                filterCriteria,
                page,
                size
        );
        return ResponseEntity.ok(filteredPaySlips);
    }

    @GetMapping("/salary-ranges")
    public ResponseEntity<ApiResponseDTO<List<AnnualGrossRemunerationDTO>>> getEmployeeCountByAnnualSalaryRange(@RequestParam Long year) {
        ApiResponseDTO<List<AnnualGrossRemunerationDTO>> salaryRanges = paySlipService.getEmployeeCountByAnnualSalaryRange(year);
        return ResponseEntity.ok(salaryRanges);
    }

    @GetMapping("/tax-deduction-remuneration")
    public ResponseEntity<ApiResponseDTO<TaxAndRemunerationReportDTO>> getEmployeeTaxDeductionAndRemunerationReport(
            @RequestParam Long year) {
        ApiResponseDTO<TaxAndRemunerationReportDTO> response =
                paySlipService.getEmployeeTaxDeductionAndRemunerationReport(year);
        return ResponseEntity.ok(response);
    }
}
