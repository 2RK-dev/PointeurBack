package io.github.two_rk_dev.pointeurback.security;

import io.github.two_rk_dev.pointeurback.dto.CreateUserDTO;
import io.github.two_rk_dev.pointeurback.dto.UserCreatedDTO;
import io.github.two_rk_dev.pointeurback.dto.UserDTO;
import io.github.two_rk_dev.pointeurback.exception.UserNotFoundException;
import io.github.two_rk_dev.pointeurback.mapper.UserMapper;
import io.github.two_rk_dev.pointeurback.model.User;
import io.github.two_rk_dev.pointeurback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .roles(user.getRole())
                .build();
    }

    public List<UserDTO> getAll() {
        return userRepository.findAllByRoleNot("SUPERADMIN").stream()
                .map(userMapper::toDto)
                .toList();
    }

    public UserCreatedDTO create(CreateUserDTO dto) {
        User user = userMapper.fromCreateDTO(dto);
        user.setRole("ADMIN");
        String password = UUID.randomUUID().toString().replace("-", "");
        user.setPassword(passwordEncoder.encode(password));
        User saved = userRepository.save(user);
        return userMapper.toCreatedDTO(saved, password);
    }

    public UserDTO getById(Long userId) {
        User user = userRepository.findById(userId)
                .filter(u -> !u.isSuperAdmin())
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return userMapper.toDto(user);
    }

    public void deleteUser(Long userId) {
        userRepository.deleteUserByIdAndRoleIsNot(userId, "SUPERADMIN");
    }
}
