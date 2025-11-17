package io.github.two_rk_dev.pointeurback;

import io.github.two_rk_dev.pointeurback.config.AuthProperties;
import io.github.two_rk_dev.pointeurback.config.CorsProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties({AuthProperties.class, CorsProperties.class})
public class PointeurBackApplication {

    public static void main(String[] args) {
        SpringApplication.run(PointeurBackApplication.class, args);
    }

}
