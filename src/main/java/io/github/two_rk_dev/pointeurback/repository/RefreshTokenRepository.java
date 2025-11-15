package io.github.two_rk_dev.pointeurback.repository;

import io.github.two_rk_dev.pointeurback.model.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RefreshTokenRepository extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByDeviceIdAndUser_Username(String deviceId, String userUsername);

    long countByUser_Username(String userUsername);

    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByDeviceIdAndUser_UsernameAndRevokedFalse(String deviceId, String userUsername);

    Optional<RefreshToken> findByDeviceIdAndTokenAndRevokedIsFalse(String deviceId, String token);
}
