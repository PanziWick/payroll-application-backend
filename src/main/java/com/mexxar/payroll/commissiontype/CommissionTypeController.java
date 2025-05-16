package com.mexxar.payroll.commissiontype;

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
@RequestMapping("/api/commissionType")
public class CommissionTypeController {

    private final CommissionTypeService commissionTypeService;

    public CommissionTypeController(CommissionTypeService commissionTypeService) {
        this.commissionTypeService = commissionTypeService;
    }

    private static final Logger logger = LogManager.getLogger(CommissionTypeController.class);

    @Operation(summary = "Create a new commission", description = "This endpoint creates a new commission.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the commission"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<CommissionTypeResponseDTO>> createCommissionType(@Valid @RequestBody CommissionTypeRequestDTO commissionTypeRequestDTO) {
        logger.info("Received request to create commission");
        ApiResponseDTO<CommissionTypeResponseDTO> commissionTypeResponseDTO = commissionTypeService.createCommissionType(commissionTypeRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(commissionTypeResponseDTO);
    }

    @Operation(summary = "Get commission by ID", description = "This endpoint returns a commission by its given ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the commission"),
            @ApiResponse(responseCode = "404", description = "Commission not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CommissionTypeResponseDTO>> getCommissionTypeById(@PathVariable Long id) {
        logger.info("Received request to get commission by id");
        ApiResponseDTO<CommissionTypeResponseDTO> commissionTypeResponseDTO = commissionTypeService.getCommissionTypeById(id);
        return ResponseEntity.ok(commissionTypeResponseDTO);
    }

    @Operation(summary = "Get all commissions", description = "This endpoint returns a list of all commission.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved commission list"),
            @ApiResponse(responseCode = "204", description = "No content, no commissions found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<CommissionTypeResponseDTO>>> getAllCommissionTypes() {
        logger.info("Received request to get all commissions");
        ApiResponseDTO<List<CommissionTypeResponseDTO>> commissionTypeResponseDTOList = commissionTypeService.getAllCommissionTypes();

        if (commissionTypeResponseDTOList.getData().isEmpty()) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.ok(commissionTypeResponseDTOList);
        }
    }

    @Operation(summary = "Update an commission", description = "This endpoint updates an existing commission.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the commission"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Commission not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<CommissionTypeResponseDTO>> updateCommissionType(@Valid @PathVariable Long id, @RequestBody CommissionTypeRequestDTO commissionTypeRequestDTO) {
        logger.info("Received request to update commission by id");
        ApiResponseDTO<CommissionTypeResponseDTO> updatedCommissionTypeResponseDTO = commissionTypeService.updateCommissionType(id, commissionTypeRequestDTO);
        return ResponseEntity.ok(updatedCommissionTypeResponseDTO);
    }

    @Operation(summary = "Delete a commission", description = "This endpoint deletes a commission")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the commission"),
            @ApiResponse(responseCode = "404", description = "Commission not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteCommissionType(@PathVariable Long id) {
        logger.info("Received request to delete commission by id");
        ApiResponseDTO<Void> response = commissionTypeService.deleteCommissionType(id);
        return ResponseEntity.ok(response);
    }
}
