package com.mexxar.payroll.bankaccount;

import com.mexxar.payroll.common.ApiResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bank-accounts")
@RequiredArgsConstructor
public class BankAccountController {

    private final BankAccountService bankAccountService;

    private static final Logger logger = LogManager.getLogger(BankAccountController.class);

    @Operation(summary = "Create a new bank account", description = "This endpoint creates a new bank account for an employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the bank account"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Employee not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate account number for the employee")
    })
    @PostMapping("/employees/{employeeId}")
    public ResponseEntity<ApiResponseDTO<BankAccountResponseDTO>> createBankAccount(
            @PathVariable Long employeeId,
            @Valid @RequestBody BankAccountRequestDTO request) {
        logger.info("Received request to create a bank account for employee ID: {}", employeeId);
        ApiResponseDTO<BankAccountResponseDTO> response = bankAccountService.createBankAccount(employeeId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get bank account by ID", description = "This endpoint returns a bank account by its given ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the bank account"),
            @ApiResponse(responseCode = "404", description = "Bank account not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<BankAccountResponseDTO>> getBankAccountById(@PathVariable Long id) {
        logger.info("Received request to get a bank account by ID: {}", id);
        ApiResponseDTO<BankAccountResponseDTO> response = bankAccountService.getBankAccountById(id);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all bank accounts", description = "This endpoint returns a list of all bank accounts.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the bank account list"),
            @ApiResponse(responseCode = "204", description = "No content, no bank accounts found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<BankAccountResponseDTO>>> getAllBankAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        logger.info("Received request to get all bank accounts");
        ApiResponseDTO<Page<BankAccountResponseDTO>> response = bankAccountService.getAllBankAccounts(page, size);

        if (response.getData().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(response);
        }
    }

    @Operation(summary = "Update a bank account", description = "This endpoint updates an existing bank account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the bank account"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Bank account not found"),
            @ApiResponse(responseCode = "409", description = "Duplicate account number for the employee")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<BankAccountResponseDTO>> updateBankAccount(
            @PathVariable Long id,
            @Valid @RequestBody BankAccountRequestDTO request) {
        logger.info("Received request to update a bank account with ID: {}", id);
        ApiResponseDTO<BankAccountResponseDTO> response = bankAccountService.updateBankAccount(id, request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Change the account type of a bank account", description = "This endpoint updates the account type of an existing bank account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the account type"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Bank account not found")
    })
    @PatchMapping("/{id}/account-type")
    public ResponseEntity<ApiResponseDTO<BankAccountResponseDTO>> changeAccountType(
            @PathVariable Long id,
            @RequestParam AccountTypeEnum newAccountType) {
        logger.info("Received request to change account type for bank account ID: {}", id);
        ApiResponseDTO<BankAccountResponseDTO> response = bankAccountService.changeAccountType(id, newAccountType);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a bank account", description = "This endpoint deletes a bank account by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the bank account"),
            @ApiResponse(responseCode = "404", description = "Bank account not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteBankAccount(@PathVariable Long id) {
        logger.info("Received request to delete a bank account with ID: {}", id);
        bankAccountService.deleteBankAccount(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Bank Account Deleted Successfully", null));
    }
}