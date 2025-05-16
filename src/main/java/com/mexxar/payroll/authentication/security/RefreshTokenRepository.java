package com.mexxar.payroll.authentication.security;

import com.mexxar.payroll.user.UserModel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUser(UserModel user);

    void deleteByUserId(Long userId);
}
