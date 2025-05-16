package com.mexxar.payroll.department;

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
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    private static final Logger logger = LogManager.getLogger(DepartmentController.class);

    @Operation(summary = "Create a new department", description = "This endpoint creates a new department.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the department"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<DepartmentResponseDTO>> createDepartment(@Valid @RequestBody DepartmentRequestDTO departmentRequestDTO) {
        logger.info("Received request to create a Department");
        ApiResponseDTO<DepartmentResponseDTO> departmentResponseDTO = departmentService.createDepartment(departmentRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(departmentResponseDTO);
    }

    @Operation(summary = "Get department by ID", description = "This endpoint returns a department by its given ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the department"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<DepartmentResponseDTO>> getDepartmentById(@PathVariable Long id) {
        logger.info("Received request to get Department by ID");
        ApiResponseDTO<DepartmentResponseDTO> departmentResponseDTO = departmentService.getDepartmentById(id);
        return ResponseEntity.ok(departmentResponseDTO);
    }

    @Operation(summary = "Get all department", description = "This endpoint returns a list of all department.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved department list"),
            @ApiResponse(responseCode = "204", description = "No content, no department found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<DepartmentResponseDTO>>> getAllDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        logger.info("Received request to get all Departments");
        ApiResponseDTO<Page<DepartmentResponseDTO>> allDepartments = departmentService.getAllDepartments(page, size);

        if (allDepartments.getData().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(allDepartments);
        }
    }

    @Operation(summary = "Get all active department", description = "This endpoint returns a list of all active department")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved active department list"),
            @ApiResponse(responseCode = "204", description = "No content, no department found"),
    })
    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<Page<DepartmentResponseDTO>>> getAllActiveDepartments(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        logger.info("Received request to get all active Departments");
        ApiResponseDTO<Page<DepartmentResponseDTO>> allActiveDepartments = departmentService.getAllActiveDepartments(page, size);

        if (allActiveDepartments.getData().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(allActiveDepartments);
        }
    }

    @Operation(summary = "Update a department", description = "This endpoint updates an existing department.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the department"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<DepartmentResponseDTO> >updateDepartment(@Valid @PathVariable Long id, @Valid @RequestBody DepartmentRequestDTO departmentRequestDTO) {
        logger.info("Received request to update the Department");
        ApiResponseDTO<DepartmentResponseDTO> updatedDepartment = departmentService.updateDepartment(id, departmentRequestDTO);
        return ResponseEntity.ok(updatedDepartment);
    }

    @Operation(summary = "Update a department status", description = "This endpoint updates an existing department status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the department status"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponseDTO<DepartmentResponseDTO>> updateDepartmentStatus(@PathVariable Long id, @RequestParam String status) {
        logger.info("Received request to update the Department status");
        ApiResponseDTO<DepartmentResponseDTO> updatedDepartmentStatus = departmentService.updateDepartmentStatus(id, status);
        return ResponseEntity.ok(updatedDepartmentStatus);
    }

    @Operation(summary = "Delete a department", description = "This endpoint deletes a department by changing its status to INACTIVE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the department"),
            @ApiResponse(responseCode = "404", description = "Department not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteDepartment(@PathVariable Long id) {
        logger.info("Received request to delete the Department");
        departmentService.deleteDepartment(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Department Deleted Successfully", null));
    }
}
