package dev.bxlab.resultset.mapper.configs;

import dev.bxlab.resultset.mapper.converters.TypeConverter;
import dev.bxlab.resultset.mapper.core.ColumnMapping;
import dev.bxlab.resultset.mapper.utils.ConverterUtils;
import dev.bxlab.resultset.mapper.utils.ReflectionUtils;
import dev.bxlab.resultset.mapper.utils.ValueUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * This class allows defining how ResultSet columns are mapped to class fields,
 * including type converters and additional attributes.
 */
public class FieldConfig {

    /**
     * The format attribute key.
     */
    public static final String FORMAT_ATTRIBUTE = "format";

    private final String columnName;
    private final TypeConverter<?> converter;
    private final Map<String, Object> attributes;

    private FieldConfig(FieldConfigBuilder builder) {
        this.columnName = builder.columnName;
        this.converter = builder.converter;
        this.attributes = builder.attributes;
    }

    /**
     * Creates a field configuration from a ColumnMapping annotation.
     *
     * @param mappingAnnotation The annotation containing the mapping configuration
     * @return A new FieldConfig instance
     * @throws ReflectiveOperationException If an error occurs when creating the converter
     */
    public static FieldConfig from(ColumnMapping mappingAnnotation) throws ReflectiveOperationException {
        String columName = ValueUtils.ifEmpty(mappingAnnotation.value(), null);

        TypeConverter<?> converter = ReflectionUtils.createInstance(mappingAnnotation.converter());
        if (ConverterUtils.isDefaultConverter(converter)) converter = null;

        Map<String, Object> attributes = new HashMap<>();
        if (!ValueUtils.isEmpty(mappingAnnotation.format()))
            attributes.put(FORMAT_ATTRIBUTE, mappingAnnotation.format());

        return FieldConfig.builder()
                .toColumn(columName)
                .withConverter(converter)
                .withAttributes(attributes)
                .build();
    }

    /**
     * Creates a new builder to configure a FieldConfig.
     *
     * @return A new FieldConfigBuilder
     */
    public static FieldConfigBuilder builder() {
        return new FieldConfigBuilder();
    }

    /**
     * Gets the configured column name.
     *
     * @return The column name or empty if not defined
     */
    public Optional<String> getColumnName() {
        return Optional.ofNullable(this.columnName);
    }

    /**
     * Gets the configured type converter.
     *
     * @return The type converter or empty if not defined
     */
    public Optional<TypeConverter<?>> getConverter() {
        return Optional.ofNullable(this.converter);
    }

    /**
     * Gets all configured attributes.
     *
     * @return A map with the configured attributes
     */
    public Map<String, Object> getAttributes() {
        return this.attributes == null ? new HashMap<>() : attributes;
    }

    /**
     * Gets an attribute value by key and type.
     * @param <T> the type of the attribute
     * @param key the attribute key
     * @param type the attribute type
     * @return an Optional containing the attribute value, or an empty Optional if the attribute is not found or has a different type
     */
    public <T> Optional<T> getAttribute(String key, Class<T> type) {
        Object value = this.attributes.get(key);
        return ValueUtils.isEmpty(value) ? Optional.empty() : Optional.of(type.cast(value));
    }

    /**
     * Builder for creating FieldConfig instances in a fluent manner.
     */
    public static class FieldConfigBuilder {
        private final Map<String, Object> attributes;

        private String columnName;
        private TypeConverter<?> converter;

        /**
         * Creates a new builder with an empty attributes map.
         */
        public FieldConfigBuilder() {
            this.attributes = new HashMap<>();
        }

        /**
         * Sets the column name for mapping.
         *
         * @param columnName Column name in the ResultSet
         * @return The builder for method chaining
         */
        public FieldConfigBuilder toColumn(String columnName) {
            this.columnName = columnName;
            return this;
        }

        /**
         * Sets the type converter for the field.
         *
         * @param converter Type converter to use
         * @return The builder for method chaining
         */
        public FieldConfigBuilder withConverter(TypeConverter<?> converter) {
            this.converter = converter;
            return this;
        }

        /**
         * Adds an attribute to the field configuration.
         *
         * @param key Attribute key
         * @param value Attribute value
         * @return The builder for method chaining
         */
        public FieldConfigBuilder withAttribute(String key, Object value) {
            this.attributes.put(key, value);
            return this;
        }

        /**
         * Adds multiple attributes to the field configuration.
         *
         * @param attributes Map of attributes to add
         * @return The builder for method chaining
         */
        public FieldConfigBuilder withAttributes(Map<String, Object> attributes) {
            this.attributes.putAll(ValueUtils.requireNonNull(attributes, "Attributes can not be null"));
            return this;
        }

        /**
         * Builds a FieldConfig instance with the established configuration.
         * Validates that the configured values are valid.
         *
         * @return A new FieldConfig instance
         * @throws IllegalArgumentException If any configured value is invalid
         */
        public FieldConfig build() {
            if (this.columnName != null) ValueUtils.requireNonEmpty(this.columnName, "Column name can not be empty");

            ValueUtils.requireNonNull(this.attributes, "Attributes can not be null");

            this.attributes.forEach((key, value) -> {
                ValueUtils.requireNonEmpty(key, "Attribute name can not be empty");
                ValueUtils.requireNonNull(value, "Attribute value can not be null");
            });

            return new FieldConfig(this);
        }
    }
}
