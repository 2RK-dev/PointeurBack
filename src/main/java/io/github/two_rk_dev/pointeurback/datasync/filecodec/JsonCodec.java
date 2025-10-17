package io.github.two_rk_dev.pointeurback.datasync.filecodec;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.two_rk_dev.pointeurback.dto.datasync.TableData;
import io.github.two_rk_dev.pointeurback.exception.InvalidFileFormatException;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Unmodifiable;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Component("json_codec")
public class JsonCodec implements FileCodec {
    private final ObjectMapper objectMapper;

    public JsonCodec(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public byte[] encode(List<TableData> dataSet) throws IOException {
        LinkedHashMap<String, Object> out = new LinkedHashMap<>();
        for (TableData tableData : dataSet) {
            out.put(
                    tableData.tableName(),
                    tableData.rows().stream()
                            .map(r -> rowToLinkedHashmap(r, tableData.headers()))
                            .toList()
            );
        }
        return objectMapper.writeValueAsBytes(out);
    }

    private static @NotNull LinkedHashMap<String, String> rowToLinkedHashmap(@NotNull List<String> row, @NotNull List<String> headers) {
        return IntStream.range(0, row.size())
                .boxed()
                .collect(Collectors.toMap(headers::get, row::get, (oldV, newV) -> newV, LinkedHashMap::new));
    }

    @Override
    public List<@NotNull TableData> decode(InputStream inputStream) throws IOException {
        JsonNode root = objectMapper.readTree(inputStream);
        if (!root.isObject()) throw new InvalidFileFormatException("Expected JSON object at the root", null);
        return root.properties().stream().map(this::parseJsonProperty).toList();
    }

    @Override
    public @NotNull Type getType() {
        return Type.JSON;
    }

    @Contract("_ -> new")
    private @NotNull TableData parseJsonProperty(Map.@NotNull Entry<String, JsonNode> property) {
        JsonNode jsonRows = property.getValue();
        String name = property.getKey();
        if (!jsonRows.isArray() || !jsonRows.elements().hasNext()) return new TableData(name, List.of(), List.of());
        Iterator<JsonNode> rowsIter = jsonRows.elements();
        JsonNode first = rowsIter.next();
        if (!first.isObject()) return new TableData(name, List.of(), List.of());
        List<String> headers = first.propertyStream().map(Map.Entry::getKey).toList();
        List<List<String>> rows = jsonRows.valueStream()
                .filter(JsonNode::isObject)
                .map(jsonRow -> extractHeaderValues(jsonRow, headers))
                .toList();
        return new TableData(name, headers, rows);
    }

    /**
     * @param jsonRow JSON object node containing the row data
     * @param headers ordered list of header names to extract
     * @return unmodifiable list of string values corresponding to the headers, preserving the order of the headers
     */
    private static @NotNull @Unmodifiable List<String> extractHeaderValues(JsonNode jsonRow, @NotNull List<String> headers) {
        return headers.stream()
                .map(h -> {
                    JsonNode node = jsonRow.get(h);
                    return node == null || node.isNull() ? null : node.asText();
                })
                .toList();
    }
}
