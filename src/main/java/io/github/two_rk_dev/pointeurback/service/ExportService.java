package io.github.two_rk_dev.pointeurback.service;

import io.github.two_rk_dev.pointeurback.datasync.filecodec.FileCodec;

import java.io.IOException;
import java.util.List;

public interface ExportService {
    Exported export(List<String> entitiesNames, String format) throws IOException;

    record Exported(byte[] data, FileCodec.Type fileCodec) {
    }
}
