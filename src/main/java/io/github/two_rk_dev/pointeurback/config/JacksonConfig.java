package io.github.two_rk_dev.pointeurback.config;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.NumberDeserializers;
import com.fasterxml.jackson.databind.deser.std.StdScalarDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;

import java.io.IOException;
import java.math.BigDecimal;

@Configuration
public class JacksonConfig {

    @Bean("fileParsingObjectMapper")
    public ObjectMapper fileParsingObjectMapper(Jackson2ObjectMapperBuilder builder) {
        SimpleModule module = new SimpleModule();
        JsonDeserializer<Long> defaultLong = new NumberDeserializers.LongDeserializer(Long.class, null);
        JsonDeserializer<Long> flexibleLong = new StdScalarDeserializer<>(Long.class) {
            @Override
            public Long deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
                try {
                    return defaultLong.deserialize(p, ctx);
                } catch (IllegalArgumentException | JacksonException e) {
                    String value = p.getText().trim();
                    if (value.isEmpty()) return null;

                    try {
                        BigDecimal bd = new BigDecimal(value);
                        if (bd.stripTrailingZeros().scale() > 0) {
                            throw ctx.weirdStringException(value, Long.class, "Value has fractional parts: " + value);
                        }

                        return bd.longValueExact();
                    } catch (NumberFormatException | ArithmeticException ex) {
                        throw ctx.weirdStringException(value, Long.class, "Value out of range or not a valid number: " + value);
                    }
                }
            }

            @Override
            public Long getNullValue(DeserializationContext ctxt) {
                return null;
            }
        };

        module.addDeserializer(Long.class, flexibleLong);

        return builder.createXmlMapper(false).build().registerModule(module);
    }
}