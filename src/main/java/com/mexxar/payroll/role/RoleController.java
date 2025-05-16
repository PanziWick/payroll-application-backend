package com.mexxar.payroll.role;

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
import java.util.Set;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final RoleService roleService;

    public RoleController(RoleService roleService) {
        this.roleService = roleService;
    }

    private static final Logger logger = LogManager.getLogger(RoleController.class);

    @Operation(summary = "Create a new role", description = "This endpoint creates a new role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the role"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<RoleResponseDTO>> createRole(@Valid @RequestBody RoleRequestDTO request) {
        logger.info("Received request to create role: {}", request);
        ApiResponseDTO<RoleResponseDTO> response = roleService.createRole(request);
        logger.info("Role created successfully: {}", response);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @Operation(summary = "Add permissions to a role", description = "This endpoint adds permissions to an existing role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully added permissions to the role"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @PostMapping("/{roleId}/permissions")
    public ResponseEntity<ApiResponseDTO<RoleResponseDTO>> addPermissionsToRole(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        logger.info("Received request to add permissions to role with ID: {}. Permissions: {}", roleId, permissionIds);
        ApiResponseDTO<RoleResponseDTO> response = roleService.addPermissionsToRole(roleId, permissionIds);
        logger.info("Permissions added successfully to role ID: {}. Updated role: {}", roleId, response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get role by ID", description = "This endpoint retrieves a role by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the role"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RoleResponseDTO>> getRoleById(@PathVariable Long id) {
        logger.info("Received request to get role by ID: {}", id);
        ApiResponseDTO<RoleResponseDTO> response = roleService.getRoleById(id);
        logger.info("Role retrieved: {}", response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Get all roles", description = "This endpoint returns a list of all roles.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved role list"),
            @ApiResponse(responseCode = "204", description = "No content, no roles found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<RoleResponseDTO>>> getAllRoles() {
        logger.info("Received request to get all roles");
        ApiResponseDTO<List<RoleResponseDTO>> response = roleService.getAllRoles();
        logger.info("All roles retrieved: {}", response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Update a role", description = "This endpoint updates an existing role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the role"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<RoleResponseDTO>> updateRole(@Valid @PathVariable Long id, @RequestBody RoleRequestDTO roleDetails) {
        logger.info("Received request to update role with ID: {}. Update data: {}", id, roleDetails);
        ApiResponseDTO<RoleResponseDTO> response = roleService.updateRole(id, roleDetails);
        logger.info("Role updated successfully: {}", response);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "Delete a role", description = "This endpoint deletes a role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the role"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRole(@PathVariable Long id) {
        logger.info("Received request to delete role with ID: {}", id);
        roleService.deleteRole(id);
        logger.info("Role with ID: {} deleted successfully", id);
        return ResponseEntity.ok("Role deleted successfully");
    }

    @Operation(summary = "Remove permissions from a role", description = "This endpoint removes permissions from an existing role.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully removed permissions from the role"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @PostMapping("/{roleId}/remove-permissions")
    public ResponseEntity<ApiResponseDTO<RoleResponseDTO>> removePermissionsFromRole(@PathVariable Long roleId, @RequestBody Set<Long> permissionIds) {
        logger.info("Received request to remove permissions from role with ID: {}. Permissions: {}", roleId, permissionIds);
        ApiResponseDTO<RoleResponseDTO> updatedRole = roleService.removePermissionsFromRole(roleId, permissionIds);
        logger.info("Permissions removed successfully from role ID: {}. Updated role: {}", roleId, updatedRole);
        return ResponseEntity.ok(updatedRole);
    }

    @Operation(summary = "Save permissions from a role", description = "This endpoint save permissions from an existing role.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully saved permissions from the role"),
        @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
        @ApiResponse(responseCode = "404", description = "Role not found")
    })
    @PutMapping("/{roleId}/save-permissions")
    public ResponseEntity<ApiResponseDTO<RoleResponseDTO>> savePermissionsFromRole(@PathVariable Long roleId, @RequestBody List<Long> permissionIds) {
        logger.info("Received request to save permissions from role with ID: {}. Permissions: {}", roleId, permissionIds);
        ApiResponseDTO<RoleResponseDTO> updatedRole = roleService.savePermissionsToRole(roleId, permissionIds);
        logger.info("Permissions saved successfully from role ID: {}. Updated role: {}", roleId, updatedRole);
        return ResponseEntity.ok(updatedRole);
    }
}
