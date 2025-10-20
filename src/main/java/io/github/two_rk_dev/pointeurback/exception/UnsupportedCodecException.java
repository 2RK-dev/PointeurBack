package io.github.two_rk_dev.pointeurback.exception;

import io.github.two_rk_dev.pointeurback.datasync.filecodec.FileCodec;

import java.util.Arrays;

/**
 * Thrown when a requested file codec/format is not supported by the application.
 * <p>
 * The exception message contains the invalid codec name and the list of available
 * codecs returned by {@link FileCodec.Type#availableCodecs()} to aid debugging or user feedback.
 *
 * @see FileCodec.Type
 * @see InvalidFileFormatException
 */
public class UnsupportedCodecException extends RuntimeException {

    /**
     * @param codecName the unsupported codec name provided by the caller
     */
    public UnsupportedCodecException(String codecName) {
        super("Unsupported codec: " + codecName +
              ". Available codecs: " + Arrays.toString(FileCodec.Type.availableCodecs())
        );
    }
}