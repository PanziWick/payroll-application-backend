package com.mexxar.payroll.designation;

import com.mexxar.payroll.common.ApiResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/designations")
public class DesignationController {

    private final DesignationService designationService;

    public DesignationController(DesignationService designationService) {
        this.designationService = designationService;
    }

    private static final Logger logger = LogManager.getLogger(DesignationController.class);

    @Operation(summary = "Create a new designation", description = "This endpoint creates a new designation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the designation"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<DesignationResponseDTO>> createDesignation(@Valid @RequestBody DesignationRequestDTO designationDTO) {
        logger.info("Received request to create a Designation");
        ApiResponseDTO<DesignationResponseDTO> designationResponseDTO = designationService.createDesignation(designationDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(designationResponseDTO);
    }

    @Operation(summary = "Get designation by ID", description = "This endpoint returns a designation by its given ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the designation"),
            @ApiResponse(responseCode = "404", description = "Designation not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<DesignationResponseDTO>> getDesignationById(@PathVariable Long id) {
        logger.info("Received request to get Designation by ID");
        ApiResponseDTO<DesignationResponseDTO> designationResponseDTO = designationService.getDesignationById(id);
        return ResponseEntity.ok(designationResponseDTO);
    }

    @Operation(summary = "Get all designations", description = "This endpoint returns a list of all designations.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved designations list"),
            @ApiResponse(responseCode = "204", description = "No content, no designations found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<DesignationResponseDTO>>> getAllDesignations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        logger.info("Received request to get all Designation");
        ApiResponseDTO<Page<DesignationResponseDTO>> allDesignations = designationService.getAllDesignations(page, size);

        if (allDesignations.getData().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(allDesignations);
        }
    }

    @Operation(summary = "Get all active designation", description = "This endpoint returns a list of all active designations")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved active designations list"),
            @ApiResponse(responseCode = "204", description = "No content, no designations found"),
    })
    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<Page<DesignationResponseDTO>>> getAllActiveDesignations(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        logger.info("Received request to get all active Designation");
        ApiResponseDTO<Page<DesignationResponseDTO>> activeDesignations = designationService.getAllActiveDesignations(page, size);

        if (activeDesignations.getData().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(activeDesignations);
        }
    }

    @Operation(summary = "Update a designation", description = "This endpoint updates an existing designation.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the designation"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Designation not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<DesignationResponseDTO>> updateDesignation(@Valid @PathVariable Long id, @Valid @RequestBody DesignationRequestDTO designationDetails) {
        logger.info("Received request to update the Designation");
        ApiResponseDTO<DesignationResponseDTO> updatedDesignation = designationService.updateDesignation(id, designationDetails);
        return ResponseEntity.ok(updatedDesignation);
    }

    @Operation(summary = "Update a designation status", description = "This endpoint updates an existing designation status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the designation status"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Designation not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponseDTO<DesignationResponseDTO>> updateDesignationStatus(@PathVariable Long id, @RequestParam String status) {
        logger.info("Received request to update the Designation status");
        ApiResponseDTO<DesignationResponseDTO> updatedDesignationStatus = designationService.updateDesignationStatus(id, status);
        return ResponseEntity.ok(updatedDesignationStatus);
    }

    @Operation(summary = "Delete a designation", description = "This endpoint deletes a designation by changing its status to INACTIVE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the designation"),
            @ApiResponse(responseCode = "404", description = "Designation not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteDesignation(@PathVariable Long id) {
        logger.info("Received request to delete the Designation");
        designationService.deleteDesignation(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Designation Deleted Successfully", null));
    }
}
