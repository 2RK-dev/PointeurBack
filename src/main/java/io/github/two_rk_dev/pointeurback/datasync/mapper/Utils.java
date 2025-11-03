package io.github.two_rk_dev.pointeurback.datasync.mapper;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Unmodifiable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@UtilityClass
class Utils {

    @SuppressWarnings("unchecked")
    public static <T extends Record> @NotNull T parseDTO(@NotNull List<String> row,
                                                         @NotNull List<String> headers,
                                                         @NotNull Class<T> clazz,
                                                         @NotNull Map<String, ColumnFieldBinding<T>> bindings,
                                                         ObjectMapper objectMapper) {
        Object[] args = new Object[bindings.size()];
        for (Iterator<String> rowIt = row.iterator(), headersIt = headers.iterator();
             headersIt.hasNext() && rowIt.hasNext();
        ) {
            String header = headersIt.next();
            String value = rowIt.next();
            ColumnFieldBinding<T> binding = bindings.get(header);
            if (binding != null) args[binding.order()] = objectMapper.convertValue(value, binding.fieldType());
        }
        try {
            return (T) clazz.getDeclaredConstructors()[0].newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
            log.error("Failed to parse {} from {}", clazz.getName(), Arrays.toString(args), e);
            throw new IllegalArgumentException("Failed to parse row", e);
        }
    }

    public static <T extends Record> @Nullable List<String> toRow(@NotNull T record) {
        try {
            List<String> list = new ArrayList<>();
            for (RecordComponent field : record.getClass().getRecordComponents()) {
                String value = String.valueOf(field.getAccessor().invoke(record));
                list.add(value);
            }
            return list;
        } catch (IllegalAccessException | InvocationTargetException e) {
            log.error("Unable to collect the property values of {}", record, e);
            return null;
        }
    }

    @SuppressWarnings("NullableProblems")
    public static <T extends Record> @Unmodifiable @NotNull List<@NotNull List<String>> toRows(Collection<T> records) {
        return records.stream()
                .map(Utils::toRow)
                .filter(Objects::nonNull)
                .toList();
    }

    public static <T extends Record> @NotNull Map<String, ColumnFieldBinding<T>> buildMapping(@NotNull Class<T> clazz) {
        Map<String, ColumnFieldBinding<T>> bindings = new HashMap<>();
        RecordComponent[] recordComponents = clazz.getRecordComponents();
        for (int i = 0; i < recordComponents.length; i++) {
            RecordComponent field = recordComponents[i];
            ColumnField ann = field.getAnnotation(ColumnField.class);
            String header = (ann == null || ann.header().isBlank()) ? field.getName() : ann.header();
            Function<T, String> reader = dto -> {
                try {
                    return String.valueOf(field.getAccessor().invoke(dto));
                } catch (IllegalAccessException | InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            };
            bindings.put(header, new ColumnFieldBinding<>(i, reader, field.getType()));
        }
        return bindings;
    }

    public static <T extends Record> @Unmodifiable @NotNull List<String> getHeaders(@NotNull Map<String, ColumnFieldBinding<T>> bindingMap) {
        return bindingMap.entrySet()
                .stream().sorted(Comparator.comparing(entry -> entry.getValue().order()))
                .map(Map.Entry::getKey)
                .toList();
    }

    public static String rowToString(@NotNull List<String> row, @NotNull List<String> headers) {
        return IntStream.range(0, headers.size())
                .mapToObj(i -> "%s=%s".formatted(headers.get(i), i < row.size() ? row.get(i) : ""))
                .collect(Collectors.joining(", ", "[", "]"));
    }
}