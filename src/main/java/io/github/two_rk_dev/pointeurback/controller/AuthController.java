package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.LoggedInDTO;
import io.github.two_rk_dev.pointeurback.dto.LoginRequestDTO;
import io.github.two_rk_dev.pointeurback.dto.LoginResponseDTO;
import io.github.two_rk_dev.pointeurback.dto.UserDTO;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
class AuthController {
    private final AuthService authService;

    private static @NotNull String buildDeviceIdCookie(@NotNull LoggedInDTO loggedInDTO) {
        return ResponseCookie.from("device_id", loggedInDTO.refreshToken().deviceId())
                .sameSite("Strict")
                .path("/")
                .maxAge(Integer.MAX_VALUE)
                .httpOnly(true)
                .build().toString();
    }

    private static @NotNull String buildRefreshTokenCookie(@NotNull LoggedInDTO loggedInDTO) {
        return ResponseCookie.from("refresh_token", loggedInDTO.refreshToken().token())
                .sameSite("Strict")
                .path("/")
                .maxAge(loggedInDTO.refreshToken().maxAge())
                .httpOnly(true)
                .build().toString();
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO dto,
            @CookieValue(name = "device_id", required = false) String deviceId) {

        LoggedInDTO loggedInDTO = authService.login(dto, deviceId);
        List<String> cookies = List.of(buildRefreshTokenCookie(loggedInDTO), buildDeviceIdCookie(loggedInDTO));
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.addAll(HttpHeaders.SET_COOKIE, cookies))
                .body(loggedInDTO.responseDTO());
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO> me(@AuthenticationPrincipal UserDetails userDetails) {
        String role = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(authority -> authority.startsWith("ROLE_"))
                .map(authority -> authority.substring(5).toLowerCase())
                .findFirst().orElse(null);
        return ResponseEntity.ok(new UserDTO(userDetails.getUsername(), role));
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponseDTO> refreshToken(
            @CookieValue(value = "device_id", required = false) String deviceId,
            @CookieValue(value = "refresh_token", required = false) String refreshToken) {

        if (deviceId == null || refreshToken == null) throw new BadCredentialsException("Insufficient credentials");
        LoggedInDTO refreshedSession = authService.refreshSession(deviceId, refreshToken);
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .headers(headers -> headers.set("Set-Cookie", buildRefreshTokenCookie(refreshedSession)))
                .body(refreshedSession.responseDTO());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(
            @CookieValue(value = "device_id", required = false) String deviceId,
            @CookieValue(value = "refresh_token", required = false) String refreshToken) {

        if (deviceId == null || refreshToken == null) return ResponseEntity.noContent().build();
        authService.logout(deviceId, refreshToken);
        ResponseCookie cookie = ResponseCookie.from("refresh_token", "")
                .sameSite("Strict")
                .path("/")
                .maxAge(0)
                .httpOnly(true)
                .build();
        return ResponseEntity
                .noContent()
                .headers(headers -> headers.set("Set-Cookie", cookie.toString()))
                .build();
    }
}
