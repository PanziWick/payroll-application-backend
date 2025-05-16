package com.mexxar.payroll.employee;

import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.designationmanagementhistory.DesignationManagementHistoryRequestDTO;
import com.mexxar.payroll.designationmanagementhistory.DesignationManagementHistoryResponseDTO;
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
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    private static final Logger logger = LogManager.getLogger(EmployeeController.class);

    @Operation(summary = "Create a new employee", description = "This endpoint creates a new employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully created the employee"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid")
    })
    @PostMapping
    public ResponseEntity<ApiResponseDTO<EmployeeResponseDTO>> createEmployee(@Valid @RequestBody EmployeeRequestDTO employeeRequestDTO) {
        logger.info("Received request to create an Employee");
        ApiResponseDTO<EmployeeResponseDTO> employeeResponseDTO = employeeService.createEmployee(employeeRequestDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(employeeResponseDTO);
    }

    @Operation(summary = "Get employee by ID", description = "This endpoint returns a employee by its given ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the employee"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EmployeeResponseDTO>> getEmployeeById(@PathVariable Long id) {
        logger.info("Received request to get an Employee by ID");
        ApiResponseDTO<EmployeeResponseDTO> employeeResponseDTO = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employeeResponseDTO);
    }

    @Operation(summary = "Get all employee", description = "This endpoint returns a list of all employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved employee list"),
            @ApiResponse(responseCode = "204", description = "No content, no employee found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<Page<EmployeeResponseDTO>>> getAllEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        logger.info("Received request to get all Employees");
        ApiResponseDTO<Page<EmployeeResponseDTO>> allEmployees = employeeService.getAllEmployees(page, size);

        if (allEmployees.getData().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(allEmployees);
        }
    }

    @Operation(summary = "Get all active employee", description = "This endpoint returns a list of all active employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved active employee list"),
            @ApiResponse(responseCode = "204", description = "No content, no employee found"),
    })
    @GetMapping("/active")
    public ResponseEntity<ApiResponseDTO<Page<EmployeeResponseDTO>>> getAllActiveEmployees(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        logger.info("Received request to get all active Employees");
        ApiResponseDTO<Page<EmployeeResponseDTO>> allActiveEmployees = employeeService.getAllActiveEmployees(page, size);

        if (allActiveEmployees.getData().isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } else {
            return ResponseEntity.ok(allActiveEmployees);
        }
    }

    @Operation(summary = "Update an employee", description = "This endpoint updates an existing employee.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the employee"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<EmployeeResponseDTO>> updateEmployee(
            @Valid @PathVariable Long id,
            @Valid @RequestBody EmployeeRequestDTO employeeRequestDTO) {
        logger.info("Received request to update an Employee");
        ApiResponseDTO<EmployeeResponseDTO> updatedEmployee = employeeService.updateEmployee(id, employeeRequestDTO);
        return ResponseEntity.ok(updatedEmployee);
    }

    @Operation(summary = "Update an employee status", description = "This endpoint updates an existing employee status.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the employee status"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PutMapping("/{id}/status")
    public ResponseEntity<ApiResponseDTO<EmployeeResponseDTO>> updateEmployeeStatus(@PathVariable Long id, @RequestParam String status) {
        logger.info("Received request to update an Employee status");
        ApiResponseDTO<EmployeeResponseDTO> updatedEmployeeStatus = employeeService.updateEmployeeStatus(id, status);
        return ResponseEntity.ok(updatedEmployeeStatus);
    }

    @Operation(summary = "Delete a employee", description = "This endpoint deletes a employee by changing its status to INACTIVE.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the employee"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<Void>> deleteEmployee(@PathVariable Long id) {
        logger.info("Received request to delete the Employee");
        employeeService.deleteEmployee(id);
        return ResponseEntity.ok(new ApiResponseDTO<>("Employee Deleted Successfully", null));
    }

    @GetMapping("/filter")
    public ResponseEntity<ApiResponseDTO<Page<EmployeeResponseDTO>>> filterEmployees(
            @ModelAttribute EmployeeFilterCriteria filterCriteria,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        ApiResponseDTO<Page<EmployeeResponseDTO>> filteredEmployees = employeeService.filterEmployees(
                filterCriteria,
                page,
                size
        );
        return ResponseEntity.ok(filteredEmployees);
    }

    @PutMapping("/designation/change")
    public ResponseEntity<DesignationManagementHistoryResponseDTO> updateEmployeeDesignation(
            @RequestBody DesignationManagementHistoryRequestDTO requestDTO) {

        DesignationManagementHistoryResponseDTO responseDTO = employeeService.changeEmployeeDesignation(requestDTO);
        return ResponseEntity.ok(responseDTO);
    }
}
