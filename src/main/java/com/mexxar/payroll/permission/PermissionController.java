package com.mexxar.payroll.permission;

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
@RequestMapping("/api/permissions")
public class PermissionController {

    private final PermissionService permissionService;

    public PermissionController(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    private static final Logger logger = LogManager.getLogger(PermissionController.class);

    @Operation(summary = "Create a new permission", description = "This endpoint creates a new permission.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the permission"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<PermissionResponseDTO>> createPermission(@Valid @RequestBody PermissionRequestDTO request) {
        logger.info("Received request to create permission: {}", request);
        ApiResponseDTO<PermissionResponseDTO> response = permissionService.createPermission(request);
        logger.info("Permission created successfully: {}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Get permission by ID", description = "This endpoint returns a permission by its given ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the permission"),
            @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PermissionResponseDTO>> getPermissionById(@PathVariable Long id) {
        logger.info("Received request to get permission by ID: {}", id);
        ApiResponseDTO<PermissionResponseDTO> response = permissionService.getPermissionById(id);
        logger.info("Permission retrieved: {}", response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all permissions", description = "This endpoint returns a list of all permissions.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved permission list"),
            @ApiResponse(responseCode = "204", description = "No content, no permissions found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<PermissionResponseDTO>>> getAllPermissions() {
        logger.info("Received request to get all permissions");
        ApiResponseDTO<List<PermissionResponseDTO>> response = permissionService.getAllPermissions();
        logger.info("All permissions retrieved: {}", response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update a permission", description = "This endpoint updates an existing permission.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the permission"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<PermissionResponseDTO>> updatePermission(@Valid @PathVariable Long id, @RequestBody PermissionRequestDTO request) {
        logger.info("Received request to update permission with ID: {}. Update data: {}", id, request);
        ApiResponseDTO<PermissionResponseDTO> response = permissionService.updatePermission(id, request);
        logger.info("Permission updated successfully: {}", response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a permission", description = "This endpoint deletes a permission.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the permission"),
            @ApiResponse(responseCode = "404", description = "Permission not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deletePermission(@PathVariable Long id) {
        logger.info("Received request to delete permission with ID: {}", id);
        permissionService.deletePermission(id);
        logger.info("Permission with ID: {} deleted successfully", id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Permission Deleted Successfully", null));
    }
}
