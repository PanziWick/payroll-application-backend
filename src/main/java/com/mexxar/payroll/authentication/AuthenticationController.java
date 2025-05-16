package com.mexxar.payroll.authentication;

import com.mexxar.payroll.authentication.security.*;
import com.mexxar.payroll.common.ApiResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @Operation(summary = "User Login", description = "This endpoint authenticates a user and returns a JWT token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully authenticated"),
            @ApiResponse(responseCode = "400", description = "Invalid credentials provided"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @PostMapping("/login")
    public ResponseEntity<ApiResponseDTO<LoginResponseDTO>> authenticate(@Valid @RequestBody LoginRequestDTO request) {
        ApiResponseDTO<LoginResponseDTO> userDetails = authenticationService.authenticate(request);
        return ResponseEntity.ok(userDetails);
    }

    @Operation(summary = "Refresh Token", description = "This endpoint generates a new access token using a refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully generated new access token"),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @PostMapping("/refresh-token")
    public ResponseEntity<ApiResponseDTO<TokenRefreshResponseDTO>> refreshToken(@RequestBody TokenRefreshRequestDTO request) {
        ApiResponseDTO<TokenRefreshResponseDTO> response = authenticationService.createAccessToken(request);
        return ResponseEntity.ok(response);
    }

    @Operation(summary = "User Logout", description = "This endpoint logs out a user and invalidates the refresh token.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully logged out"),
            @ApiResponse(responseCode = "400", description = "Invalid refresh token"),
            @ApiResponse(responseCode = "401", description = "Unauthorized access")
    })
    @PostMapping("/logout")
    public ResponseEntity<ApiResponseDTO<Void>> logout(@RequestBody TokenRefreshRequestDTO request) {
        ApiResponseDTO<Void> response = authenticationService.logout(request.refreshToken());
        return ResponseEntity.ok(response);
    }
}
