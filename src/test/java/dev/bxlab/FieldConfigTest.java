package dev.bxlab;

import dev.bxlab.configs.FieldConfig;
import dev.bxlab.converters.DefaultConverter;
import dev.bxlab.converters.TypeConverter;
import dev.bxlab.core.ColumnMapping;
import dev.bxlab.utils.ReflectionUtils;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class FieldConfigTest {

    @Mock
    ColumnMapping mappingAnnotation;

    @Mock
    private TypeConverter<?> mockConverter;

    @Test
    @DisplayName("Builder should create FieldConfig with valid parameters")
    void builder_ShouldCreateFieldConfig_WithValidParameters() {
        FieldConfig config = FieldConfig.builder()
                .toColumn("user_name")
                .withConverter(mockConverter)
                .withAttribute("format", "yyyy-MM-dd")
                .build();

        assertEquals("user_name", config.getColumnName().orElse(null));
        assertSame(mockConverter, config.getConverter().orElse(null));
        assertEquals(1, config.getAttributes().size());
        assertEquals("yyyy-MM-dd", config.getAttribute("format", String.class).orElse(null));
    }

    @Test
    @DisplayName("Builder should handle null values properly")
    void builder_ShouldHandleNullValues() {
        FieldConfig config = FieldConfig.builder().build();

        assertTrue(config.getColumnName().isEmpty());
        assertTrue(config.getConverter().isEmpty());
        assertNotNull(config.getAttributes());
        assertTrue(config.getAttributes().isEmpty());
    }

    @Test
    @DisplayName("Builder should throw exception when column name is empty")
    void builder_ShouldThrowException_WhenColumnNameIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> FieldConfig.builder().toColumn("").build());
    }

    @Test
    @DisplayName("Builder should throw exception when attribute key is empty or null")
    void builder_ShouldThrowException_WhenAttributeKeyIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> FieldConfig.builder().withAttribute(" ", "value").build());
        assertThrows(IllegalArgumentException.class, () -> FieldConfig.builder().withAttribute(null, "value").build());
    }

    @Test
    @DisplayName("Builder should throw exception when attribute value is null")
    void builder_ShouldThrowException_WhenAttributeValueIsEmpty() {
        assertThrows(IllegalArgumentException.class, () -> FieldConfig.builder().withAttribute("testKey", null).build());
        assertDoesNotThrow(() -> FieldConfig.builder().withAttribute("testKey", " ").build());
    }

    @Test
    @DisplayName("FieldConfig.from should build FieldConfig with empty parameters")
    void from_shouldBuildFieldConfig() throws Exception {
        when(mappingAnnotation.value()).thenReturn("");
        when(mappingAnnotation.format()).thenReturn("");
        when(mappingAnnotation.converter()).thenReturn(null);

        DefaultConverter converter = new DefaultConverter();

        try (MockedStatic<ReflectionUtils> reflectionUtilsMock = mockStatic(ReflectionUtils.class)) {
            reflectionUtilsMock.when(() -> ReflectionUtils.createInstance(DefaultConverter.class)).thenReturn(converter);

            FieldConfig config = FieldConfig.from(mappingAnnotation);

            assertTrue(config.getColumnName().isEmpty());
            assertTrue(config.getConverter().isEmpty());
            assertNotNull(config.getAttributes());
            assertTrue(config.getAttributes().isEmpty());
        }
    }
}
