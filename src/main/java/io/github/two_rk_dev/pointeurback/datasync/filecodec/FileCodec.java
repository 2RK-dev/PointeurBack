package io.github.two_rk_dev.pointeurback.datasync.filecodec;

import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.exception.InvalidFileFormatException;
import io.github.two_rk_dev.pointeurback.exception.UnsupportedCodecException;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.Accessors;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.http.MediaType;
import org.springframework.web.server.UnsupportedMediaTypeStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.List;

/**
 * Interface for encoding and decoding data to and from files. Implementations of this interface handle the conversion
 * of data between a {@link List<TableData>} and a file format.
 */
public interface FileCodec {

    /**
     * Encodes a {@link List<TableData>} into a byte array.
     *
     * @param dataSet the dataset to encode, containing table data
     * @return a byte array representing the encoded data
     * @throws IOException if an I/O error occurs during encoding
     */
    byte[] encode(List<TableData> dataSet) throws IOException;

    /**
     * Decodes tables' data from the provided input stream.
     * IO errors or malformed input are wrapped in an {@link InvalidFileFormatException}.
     *
     * @param inputStream the input stream containing encoded table data; the caller is responsible for closing it
     * @return a {@link TableData} representing the extracted data
     * @throws InvalidFileFormatException if the input cannot be read or has an invalid format
     */
    List<@NotNull TableData> decode(InputStream inputStream) throws IOException;

    @NotNull Type getType();

    @Getter
    @AllArgsConstructor
    @Accessors(fluent = true)
    enum Type {
        EXCEL("excel", MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"), "xlsx"),
        ZIP_CSV("zip_csv", MediaType.parseMediaType("text/csv"), MediaType.APPLICATION_OCTET_STREAM, "zip"),
        JSON("json", MediaType.APPLICATION_JSON, "json");

        private final String codecName;
        private final MediaType inputMediaType;
        private final MediaType outputMediaType;
        private final String outputFileExtension;

        Type(String codecName, MediaType mediaType, String outputFileExtension) {
            this(codecName, mediaType, mediaType, outputFileExtension);
        }

        @Contract(pure = true)
        public @NotNull String beanName() {
            return codecName + "_codec";
        }

        public static String @NotNull [] availableCodecs() {
            return Arrays.stream(values()).map(Type::codecName).toArray(String[]::new);
        }

        public static @NotNull @Unmodifiable List<MediaType> availableInputMediaTypes() {
            return Arrays.stream(values()).map(Type::inputMediaType).toList();
        }

        public static @NotNull Type forCodecName(String codecName) {
            return Arrays.stream(values())
                    .filter(type -> type.codecName.equals(codecName))
                    .findFirst()
                    .orElseThrow(() -> new UnsupportedCodecException(codecName));
        }

        public static @NotNull Type forInputMediaType(String mediaTypeString) {
            MediaType mediaType = MediaType.parseMediaType(mediaTypeString);
            return Arrays.stream(values())
                    .filter(type -> type.inputMediaType.equals(mediaType))
                    .findFirst()
                    .orElseThrow(() -> new UnsupportedMediaTypeStatusException(
                            "Unsupported import input : " + mediaType,
                            availableInputMediaTypes()
                    ));
        }
    }
}
