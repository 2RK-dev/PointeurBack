package io.github.two_rk_dev.pointeurback.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class ApiKeyService {

    public UserDetails getUserFromApiKey(String apiKey) {
        throw new UnsupportedOperationException("Not implemented");
    }
}
