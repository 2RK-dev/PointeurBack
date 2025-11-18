package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.config.AuthProperties;
import io.github.two_rk_dev.pointeurback.dto.LoggedInDTO;
import io.github.two_rk_dev.pointeurback.dto.LoginRequestDTO;
import io.github.two_rk_dev.pointeurback.dto.LoginResponseDTO;
import io.github.two_rk_dev.pointeurback.dto.RefreshTokenDTO;
import io.github.two_rk_dev.pointeurback.mapper.RefreshTokenMapper;
import io.github.two_rk_dev.pointeurback.mapper.UserMapper;
import io.github.two_rk_dev.pointeurback.model.RefreshToken;
import io.github.two_rk_dev.pointeurback.model.User;
import io.github.two_rk_dev.pointeurback.repository.RefreshTokenRepository;
import io.github.two_rk_dev.pointeurback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final UserRepository userRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final AuthProperties authProperties;
    private final RefreshTokenMapper refreshTokenMapper;

    public LoggedInDTO login(@NotNull LoginRequestDTO dto, @Nullable String deviceId) {
        Authentication authenticated = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                dto.username(),
                dto.password()
        ));
        User user = userRepository.findByUsername(authenticated.getName())
                .orElseThrow(() -> new IllegalStateException("The user should exist if the authentication manager have authenticated it"));
        String accessToken = jwtService.encodeToToken(user);
        RefreshToken refreshToken = Optional.ofNullable(deviceId)
                .flatMap(dId -> refreshTokenRepository.findByDeviceIdAndUser_UsernameAndRevokedFalse(dId, user.getUsername()))
                .orElseGet(() -> createNewRefreshToken(user));

        RefreshTokenDTO rotatedRefreshToken = rotateRefreshToken(refreshToken);
        return new LoggedInDTO(
                rotatedRefreshToken,
                new LoginResponseDTO(accessToken, userMapper.toInfoDto(user))
        );
    }

    public LoggedInDTO refreshSession(String deviceId, String refreshToken) {
        RefreshToken session = refreshTokenRepository.findByDeviceIdAndTokenAndRevokedIsFalse(deviceId, refreshToken)
                .filter(RefreshToken::isNotExpired)
                .orElseThrow(() -> new BadCredentialsException("Session expired"));
        String accessToken = jwtService.encodeToToken(session.getUser());
        RefreshTokenDTO rotatedRefreshToken = rotateRefreshToken(session);
        return new LoggedInDTO(
                rotatedRefreshToken,
                new LoginResponseDTO(accessToken, userMapper.toInfoDto(session.getUser()))
        );
    }

    private RefreshTokenDTO rotateRefreshToken(@NotNull RefreshToken refreshToken) {
        refreshToken.setToken(UUID.randomUUID().toString());
        Duration maxAge = Duration.ofDays(authProperties.refreshSessionExpiration());
        refreshToken.setExpiresAt(OffsetDateTime.now().plus(maxAge));
        refreshTokenRepository.save(refreshToken);
        return refreshTokenMapper.toDto(refreshToken, maxAge, authProperties.cookieSecure());
    }

    private @NotNull RefreshToken createNewRefreshToken(@NotNull User user) {
        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setDeviceId(UUID.randomUUID().toString());
        refreshToken.setCreatedAt(OffsetDateTime.now());
        refreshToken.setRevoked(false);
        return refreshToken;
    }

    public void logout(String deviceId, String refreshToken) {
        refreshTokenRepository.findByDeviceIdAndTokenAndRevokedIsFalse(deviceId, refreshToken)
                .ifPresent(refreshTokenRepository::delete);
    }
}
