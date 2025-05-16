package com.mexxar.payroll.loan;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.loanlog.LoanLogRequestDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

import java.util.List;

@RestController
@RequestMapping("/api/loans")
public class LoanController {

    private final LoanService loanService;

    public LoanController(LoanService loanService) {
        this.loanService = loanService;
    }

    private static final Logger logger = LogManager.getLogger(LoanController.class);

    @Operation(summary = "Create a new loan record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Loan record created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid loan data provided")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<LoanResponseDTO>> createLoan(@Valid @RequestBody LoanRequestDTO loanRequestDTO) {
        logger.info("Received request to create a new Loan record");
        ApiResponseDTO<LoanResponseDTO> createdLoan = loanService.createLoan(loanRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdLoan);
    }

    @Operation(summary = "Get a loan by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved loan"),
            @ApiResponse(responseCode = "404", description = "Loan record not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<LoanResponseDTO>> getLoanById(@PathVariable Long id) {
        logger.info("Received request to get Loan by ID: {}", id);
        ApiResponseDTO<LoanResponseDTO> loan = loanService.getLoanById(id);
        return ResponseEntity.ok(loan);
    }

    @Operation(summary = "Get loans by Employee ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved loans by Employee ID"),
            @ApiResponse(responseCode = "404", description = "No loan records found for Employee ID")
    })
    @GetMapping("/employee/{employeeId}")
    public ResponseEntity<ApiResponseDTO<List<LoanResponseDTO>>> getLoansByEmployeeId(@PathVariable Long employeeId) {
        logger.info("Received request to get Loans by Employee ID: {}", employeeId);
        ApiResponseDTO<List<LoanResponseDTO>> loans = loanService.getLoansByEmployeeId(employeeId);
        return loans.getData().isEmpty() ? ResponseEntity.status(HttpStatus.NOT_FOUND).build() : ResponseEntity.ok(loans);
    }

    @Operation(summary = "Get all loan records with pagination")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved loan records"),
            @ApiResponse(responseCode = "204", description = "No loan records found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<LoanResponseDTO>>> getAllLoans(Pageable pageable) {
        logger.info("Received request to get all Loan records");
        ApiResponseDTO<Page<LoanResponseDTO>> loans = loanService.getAllLoans(pageable);
        return loans.getData().isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(loans);
    }

    @Operation(summary = "Update a loan record")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan record updated successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid loan data provided"),
            @ApiResponse(responseCode = "404", description = "Loan record not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<LoanResponseDTO>> updateLoan(@Valid @PathVariable Long id, @Valid @RequestBody LoanRequestDTO loanRequestDTO) {
        logger.info("Received request to update Loan record with ID: {}", id);
        ApiResponseDTO<LoanResponseDTO> updatedLoan = loanService.updateLoan(id, loanRequestDTO);
        return ResponseEntity.ok(updatedLoan);
    }

    @Operation(summary = "Delete a loan record by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Loan record deleted successfully"),
            @ApiResponse(responseCode = "404", description = "Loan record not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteLoan(@PathVariable Long id) {
        logger.info("Received request to delete Loan record with ID: {}", id);
        ApiResponseDTO<Void> response = loanService.deleteLoan(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Hold loan deduction for a loan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan hold log created successfully"),
            @ApiResponse(responseCode = "404", description = "Loan not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input or loan status not suitable for hold")
    })
    @PostMapping("/hold-loan-deduction")
    public ResponseEntity<ApiResponseDTO<LoanResponseDTO>> holdLoanDeduction(@RequestBody LoanLogRequestDTO loanLogRequestDTO) {
        ApiResponseDTO<LoanResponseDTO> loanResponseDTO = loanService.holdLoanDeduction(loanLogRequestDTO);
        return ResponseEntity.ok(loanResponseDTO);
    }

    @Operation(summary = "Release loan hold for a loan")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Loan status updated to ONGOING"),
            @ApiResponse(responseCode = "404", description = "Loan not found"),
            @ApiResponse(responseCode = "400", description = "Loan is not on hold")
    })
    @PutMapping("/release-loan-deduction/{loanId}")
    public ResponseEntity<ApiResponseDTO<LoanResponseDTO>> releaseLoanHold(@PathVariable Long loanId) {
        ApiResponseDTO<LoanResponseDTO> loanResponseDTO = loanService.releaseLoanHold(loanId);
        return ResponseEntity.ok(loanResponseDTO);
    }
}
