package io.github.two_rk_dev.pointeurback.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.OffsetDateTime;
import java.util.UUID;

@Getter
@Setter
@ToString
@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "refresh_token_id")
    private UUID id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "user_id")
    private User user;

    private String token;

    private String deviceId;

    private OffsetDateTime expiresAt;

    private OffsetDateTime createdAt;

    private boolean revoked;

    public boolean isNotExpired() {
        return expiresAt.isAfter(OffsetDateTime.now());
    }
}
