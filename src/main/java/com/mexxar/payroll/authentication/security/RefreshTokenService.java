package com.mexxar.payroll.authentication.security;

import com.mexxar.payroll.authentication.exception.TokenRefreshException;
import com.mexxar.payroll.user.UserModel;
import com.mexxar.payroll.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Value("${jwt.refresh.token.duration.ms}") //set for 2 minutes for temporary testing purpose
    private Long refreshTokenDurationMs;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository, UserService userService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userService = userService;
    }

    private static final String EXPIRED_REFRESH_TOKEN = "Expired refresh token. Please login again.";

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public Optional<RefreshToken> findByUser(UserModel user) {
        return refreshTokenRepository.findByUser(user);
    }

    public RefreshToken save(RefreshToken refreshToken) {
        return refreshTokenRepository.save(refreshToken);
    }

    public void delete(RefreshToken refreshToken) {
        refreshTokenRepository.delete(refreshToken);
    }

    // Method to verify expiration of a single token
    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(token);
            throw new TokenRefreshException(EXPIRED_REFRESH_TOKEN);
        }
        return token;
    }

    public void deleteByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new TokenRefreshException("Token not found"));
        refreshTokenRepository.delete(refreshToken); // Revoke token
    }

    public RefreshToken createRefreshToken(String username) {
        UserModel user = userService.findUserModelByEmail(username);
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString()); //This expression generates a universally unique identifier and converts it to a string.
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        return this.save(refreshToken);
    }

}
