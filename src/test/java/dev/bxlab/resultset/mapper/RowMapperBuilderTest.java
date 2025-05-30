package dev.bxlab.resultset.mapper;

import dev.bxlab.resultset.mapper.configs.NamingStrategy;
import dev.bxlab.resultset.mapper.converters.TypeConverter;
import dev.bxlab.resultset.mapper.core.ResultSetMapper;
import dev.bxlab.resultset.mapper.core.RowMapper;
import dev.bxlab.resultset.mapper.core.RowMapperBuilder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(MockitoExtension.class)
public class RowMapperBuilderTest {

    @Mock
    private TypeConverter<String> mockStringConverter;

    @Test
    void shouldCreateBuilderWithCorrectTargetType() {
        RowMapperBuilder<TestDTO> builder = RowMapperBuilder.forType(TestDTO.class);

        assertNotNull(builder);
        assertEquals(TestDTO.class, builder.getTargetType());
    }

    @Test
    void shouldCreateRowMapperInstance() {
        ResultSetMapper<TestDTO> mapper = RowMapperBuilder.forType(TestDTO.class).build();

        assertNotNull(mapper);
        assertInstanceOf(RowMapper.class, mapper);
    }

    @Test
    void shouldHaveCorrectDefaultValues() {
        RowMapperBuilder<TestDTO> builder = RowMapperBuilder.forType(TestDTO.class);

        assertEquals(NamingStrategy.AS_IS, builder.getNamingStrategy());
        assertTrue(builder.isIgnoreUnknowTypes());
        assertTrue(builder.isIgnoreUnknownColumns());
        assertTrue(builder.isCaseInsensitiveColumns());
        assertTrue(builder.isIncludeDefaultConverters());
        assertNotNull(builder.getFieldConfigs());
        assertTrue(builder.getFieldConfigs().isEmpty());
        assertNotNull(builder.getConverters());
    }

    @Test
    void shouldSetNamingStrategy() {
        RowMapperBuilder<TestDTO> builder = RowMapperBuilder.forType(TestDTO.class)
                .withNamingStrategy(NamingStrategy.SNAKE_CASE);

        assertEquals(NamingStrategy.SNAKE_CASE, builder.getNamingStrategy());
    }

    @Test
    void shouldSetFlagsCorrectly() {
        RowMapperBuilder<TestDTO> builder = RowMapperBuilder.forType(TestDTO.class)
                .ignoreUnknownTypes(false)
                .ignoreUnknownColumns(false)
                .caseInsensitiveColumns(false)
                .includeDefaultConverters(false);

        assertAll("Verify all flags are set to false",
                () -> assertFalse(builder.isIgnoreUnknowTypes()),
                () -> assertFalse(builder.isIgnoreUnknownColumns()),
                () -> assertFalse(builder.isCaseInsensitiveColumns()),
                () -> assertFalse(builder.isIncludeDefaultConverters())
        );
    }

    @Test
    void shouldAddFieldConfiguration() {
        RowMapperBuilder<TestDTO> builder = RowMapperBuilder.forType(TestDTO.class)
                .mapField("name", config -> config.toColumn("user_name"));

        assertNotNull(builder.getFieldConfigs());
        assertEquals(1, builder.getFieldConfigs().size());
        assertTrue(builder.getFieldConfigs().containsKey("name"));
        assertEquals("user_name", builder.getFieldConfigs().get("name").getColumnName().orElse(null));
    }

    @Test
    void shouldAddConverterToRegistry() {
        RowMapperBuilder<TestDTO> builder = RowMapperBuilder.forType(TestDTO.class)
                .registerConverter(String.class, mockStringConverter);

        Map<Class<?>, TypeConverter<?>> registry = builder.getConverters();
        assertNotNull(registry);
        assertNotNull(registry.get(String.class));
        assertSame(mockStringConverter, registry.get(String.class));
    }

    private static class TestDTO {
    }
}