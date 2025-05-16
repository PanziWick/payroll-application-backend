package com.mexxar.payroll.allowancetype;

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
@RequestMapping("/api/allowanceType")
public class AllowanceTypeController {

    private final AllowanceTypeService allowanceTypeService;

    public AllowanceTypeController(AllowanceTypeService allowanceTypeService) {
        this.allowanceTypeService = allowanceTypeService;
    }

    private static final Logger logger = LogManager.getLogger(AllowanceTypeController.class);

    @Operation(summary = "Create a new allowance", description = "This endpoint creates a new allowance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the allowance"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<AllowanceTypeResponseDTO>> createAllowanceType(@Valid @RequestBody AllowanceTypeRequestDTO allowanceTypeRequestDTO) {
        logger.info("Received request to create allowance");
        ApiResponseDTO<AllowanceTypeResponseDTO> allowanceTypeResponseDTO = allowanceTypeService.createAllowanceType(allowanceTypeRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(allowanceTypeResponseDTO);
    }

    @Operation(summary = "Get Allowance by ID", description = "This endpoint returns a allowance by given ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the allowance"),
            @ApiResponse(responseCode = "404", description = "Allowance not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<AllowanceTypeResponseDTO>> getAllowanceTypeById(@PathVariable Long id) {
        logger.info("Received request to get allowance by id");
        ApiResponseDTO<AllowanceTypeResponseDTO> allowanceTypeResponseDTO = allowanceTypeService.getAllowanceTypeById(id);
        return ResponseEntity.ok(allowanceTypeResponseDTO);
    }

    @Operation(summary = "Get all allowance", description = "This endpoint returns a list of all allowance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved allowance list"),
            @ApiResponse(responseCode = "204", description = "No content, no allowances found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<AllowanceTypeResponseDTO>>> getAllAllowanceTypes() {
        logger.info("Received request to get all allowances");
        ApiResponseDTO<List<AllowanceTypeResponseDTO>> allowanceTypeResponseDTOList = allowanceTypeService.getAllAllowanceTypes();

        if (allowanceTypeResponseDTOList.getData().isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(allowanceTypeResponseDTOList);
        }
    }

    @Operation(summary = "Update an allowance", description = "This endpoint updates an existing allowance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully update the allowance"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Allowance not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<AllowanceTypeResponseDTO>> updateAllowanceType(@Valid @PathVariable Long id, @RequestBody AllowanceTypeRequestDTO allowanceTypeRequestDTO) {
        logger.info("Received request to update allowance by id");
        ApiResponseDTO<AllowanceTypeResponseDTO> updatedAllowanceTypeResponseDTO = allowanceTypeService.updateAllowanceType(id, allowanceTypeRequestDTO);
        return ResponseEntity.ok(updatedAllowanceTypeResponseDTO);
    }

    @Operation(summary = "Delete an allowance", description = "This endpoint deletes an allowance")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the allowance"),
            @ApiResponse(responseCode = "404", description = "Allowance not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteAllowanceType(@PathVariable Long id) {
        logger.info("Received request to delete allowance by id");
        ApiResponseDTO<Void> response = allowanceTypeService.deleteAllowanceType(id);
        return ResponseEntity.ok(response);
    }
}
