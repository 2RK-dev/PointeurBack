package io.github.two_rk_dev.pointeurback.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImportService {
    void import_(String entityType, MultipartFile file) throws IOException;
}
