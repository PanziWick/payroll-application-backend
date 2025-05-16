package com.mexxar.payroll.user;

import com.mexxar.payroll.authentication.PasswordResetRequestDTO;
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
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    private static final Logger logger = LogManager.getLogger(UserController.class);

    @Operation(summary = "Register a new user", description = "This endpoint registers a new user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Successfully registered the user"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid")
    })
    @PostMapping("/register")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> registerUser(@Valid @RequestBody UserRequestDTO request) {
        logger.info("Received request to register a new user: {}", request);
        ApiResponseDTO<UserResponseDTO> userResponseDTO = userService.registerUser(request);
        logger.info("User registered successfully: {}", userResponseDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(userResponseDTO);
    }

    @Operation(summary = "Assign roles to a user", description = "This endpoint assigns roles to an existing user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully assigned roles to the user"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/{userId}/roles")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> assignRolesToUser(@PathVariable Long userId, @RequestBody List<Long> roleIds) {
        logger.info("Received request to assign roles to user with ID: {}. Role IDs: {}", userId, roleIds);
        ApiResponseDTO<UserResponseDTO> userResponseDTO = userService.assignRolesToUser(userId, roleIds);
        logger.info("Roles assigned successfully to user ID: {}. Updated user: {}", userId, userResponseDTO);
        return ResponseEntity.ok(userResponseDTO);
    }

    @Operation(summary = "Get user by ID", description = "This endpoint retrieves a user by its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved the user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @GetMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> getUserById(@PathVariable Long id) {
        logger.info("Received request to get user by ID: {}", id);
        ApiResponseDTO<UserResponseDTO> userResponseDTO = userService.getUserById(id);
        logger.info("User retrieved: {}", userResponseDTO);
        return ResponseEntity.ok(userResponseDTO);
    }

    @Operation(summary = "Get all users", description = "This endpoint returns a list of all users.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully retrieved user list"),
            @ApiResponse(responseCode = "204", description = "No content, no users found")
    })
    @GetMapping
    public ResponseEntity<ApiResponseDTO<List<UserResponseDTO>>> getAllUsers() {
        logger.info("Received request to get all users");
        ApiResponseDTO<List<UserResponseDTO>> userResponseDTO = userService.getAllUsers();
        logger.info("All users retrieved: {}", userResponseDTO);
        return ResponseEntity.ok(userResponseDTO);
    }

    @Operation(summary = "Update a user", description = "This endpoint updates an existing user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully updated the user"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PutMapping("/{id}")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> updateUser(@Valid @PathVariable Long id, @RequestBody UserRequestDTO request) {
        logger.info("Received request to update user with ID: {}. Update data: {}", id, request);
        ApiResponseDTO<UserResponseDTO> userResponseDTO = userService.updateUser(id, request);
        logger.info("User updated successfully: {}", userResponseDTO);
        return ResponseEntity.ok(userResponseDTO);
    }

    @Operation(summary = "Delete a user", description = "This endpoint deletes a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully deleted the user"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteUser(@PathVariable Long id) {
        logger.info("Received request to delete user with ID: {}", id);
        userService.deleteUser(id);
        logger.info("User with ID: {} deleted successfully", id);
        return ResponseEntity.ok("User Deleted Successfully");
    }

    @Operation(summary = "Remove roles from a user", description = "This endpoint removes roles from an existing user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully removed roles from the user"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid"),
            @ApiResponse(responseCode = "404", description = "User not found")
    })
    @PostMapping("/{userId}/remove-roles")
    public ResponseEntity<ApiResponseDTO<UserResponseDTO>> removeRolesFromUser(@PathVariable Long userId, @RequestBody Set<Long> roleIds) {
        logger.info("Received request to remove roles from user with ID: {}. Role IDs: {}", userId, roleIds);
        ApiResponseDTO<UserResponseDTO> userResponseDTO = userService.removeRolesFromUser(userId, roleIds);
        logger.info("Roles removed successfully from user ID: {}", userResponseDTO);
        return ResponseEntity.ok(userResponseDTO);
    }

    @Operation(summary = "Reset user password", description = "This endpoint resets the password for a user.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Password reset successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input, object invalid")
    })
    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestBody PasswordResetRequestDTO request) {
        logger.info("Received request to reset password for user: {}", request);
        userService.resetPassword(request);
        logger.info("Password reset successfully for user: {}", request);
        return ResponseEntity.ok("Password reset successfully");
    }
}
