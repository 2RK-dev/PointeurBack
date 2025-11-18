package io.github.two_rk_dev.pointeurback.controller;

import io.github.two_rk_dev.pointeurback.dto.CreateUserDTO;
import io.github.two_rk_dev.pointeurback.dto.UserCreatedDTO;
import io.github.two_rk_dev.pointeurback.dto.UserDTO;
import io.github.two_rk_dev.pointeurback.security.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
class UserManagerController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAll() {
        return ResponseEntity.ok(userService.getAll());
    }

    @PostMapping
    public ResponseEntity<UserCreatedDTO> createAdmin(@Valid @RequestBody CreateUserDTO dto) {
        UserCreatedDTO createdUser = userService.create(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{userId}")
                .buildAndExpand(createdUser.id())
                .toUri();
        return ResponseEntity.created(location).body(createdUser);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getOne(@PathVariable Long userId) {
        return ResponseEntity.ok(userService.getById(userId));
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> delete(@PathVariable Long userId) {
        userService.deleteUser(userId);
        return ResponseEntity.noContent().build();
    }
}
