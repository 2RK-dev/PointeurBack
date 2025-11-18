package io.github.two_rk_dev.pointeurback.config.bootstrap;

import io.github.two_rk_dev.pointeurback.config.AuthProperties;
import io.github.two_rk_dev.pointeurback.model.User;
import io.github.two_rk_dev.pointeurback.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.env.Environment;
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
    private final Environment environment;

    @Override
    public void run(ApplicationArguments args) {
        String username = authProperties.bootstrapSuperadmin().username();
        String configuredPassword = authProperties.bootstrapSuperadmin().password();
        boolean isTestEnvironment = environment.matchesProfiles("test");
        if (!isTestEnvironment && configuredPassword != null) {
            throw new IllegalStateException(
                    "SECURITY ERROR: 'app.auth.bootstrap-superadmin.password' is only allowed in the test profile.\n"
            );
        }
        boolean superAdminExists = userRepository.existsByUsername(username);
        if (!superAdminExists) {
            User user = new User();
            user.setUsername(username);
            user.setRole("SUPERADMIN");
            String password = isTestEnvironment ? configuredPassword : UUID.randomUUID().toString().replace("-", "");
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
