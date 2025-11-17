package io.github.two_rk_dev.pointeurback.config.bootstrap;

import io.github.two_rk_dev.pointeurback.config.AuthProperties;
import io.github.two_rk_dev.pointeurback.model.User;
import io.github.two_rk_dev.pointeurback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class SuperadminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final AuthProperties authProperties;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) {
        String username = authProperties.bootstrapSuperadmin().username();
        boolean superAdminExists = userRepository.existsByUsername(username);
        if (!superAdminExists) {
            User user = new User();
            user.setUsername(username);
            user.setRole("SUPERADMIN");
            String password = UUID.randomUUID().toString().replace("-", "");
            user.setPassword(passwordEncoder.encode(password));
            userRepository.save(user);
            log.warn("""
                            ################################################################################
                            #                         SUPERADMIN HAS BEEN ADDED                            #
                            #                 PASSWORD: {}
                            # This is the only time you will see the password, so change it immediately!   #
                            # Do not store this password at all.                                           #
                            ################################################################################
                            """,
                    password
            );
        }
    }
}
