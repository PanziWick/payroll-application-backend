package com.mexxar.payroll.authentication;

import com.mexxar.payroll.authentication.exception.TokenRefreshException;
import com.mexxar.payroll.authentication.security.*;
import com.mexxar.payroll.common.ApiResponseDTO;
import com.mexxar.payroll.user.CustomUserDetails;
import com.mexxar.payroll.user.UserModel;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthenticationService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthenticationService(AuthenticationManager authenticationManager,
                                 JwtService jwtService,
                                 RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    // Authenticate user and generate JWT token.
    public ApiResponseDTO<LoginResponseDTO> authenticate(LoginRequestDTO loginRequest) {
        // Create the authentication token
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(
                loginRequest.email(),
                loginRequest.password()
        );

        // Perform authentication
        Authentication authentication = authenticationManager.authenticate(authenticationToken);

        // If authentication is successful, generate the JWT
        UserDetails userDetails = (UserDetails) authentication.getPrincipal();
        String jwtToken = jwtService.generateToken(userDetails);

        // Generate the refresh token
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(loginRequest.email());

        // Create the login response DTO
        LoginResponseDTO loginResponseDTO = new LoginResponseDTO(
                userDetails.getUsername(),
                "Authentication successful!",
                jwtToken,
                refreshToken.getToken()
        );

        // Return the response wrapped in ApiResponseDTO
        return new ApiResponseDTO<>("Authentication successful!", loginResponseDTO);
    }

    public ApiResponseDTO<TokenRefreshResponseDTO> createAccessToken(TokenRefreshRequestDTO request) {
        String requestRefreshToken = request.refreshToken();

        // Validate and find the refresh token
        RefreshToken refreshToken = refreshTokenService.findByToken(requestRefreshToken)
                .orElseThrow(() -> new TokenRefreshException("Invalid refresh token"));

        // Verify if the refresh token is expired
        refreshTokenService.verifyExpiration(refreshToken);

        // Get the user and generate a new access token
        UserModel userModel = refreshToken.getUser();
        UserDetails userDetails = new CustomUserDetails(userModel);
        String newAccessToken = jwtService.generateToken(userDetails);

        // Create the token refresh response DTO
        TokenRefreshResponseDTO tokenRefreshResponseDTO = new TokenRefreshResponseDTO(newAccessToken);

        // Return the response wrapped in ApiResponseDTO
        return new ApiResponseDTO<>("Access token created successfully!", tokenRefreshResponseDTO);
    }

    public ApiResponseDTO<Void> logout(String refreshToken) {
        refreshTokenService.deleteByToken(refreshToken);
        return new ApiResponseDTO<>("Logout successful!", null);
    }
}
