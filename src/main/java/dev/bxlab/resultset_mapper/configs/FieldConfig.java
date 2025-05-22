package  dev.bxlab.resultset_mapper.configs;

import  dev.bxlab.resultset_mapper.converters.TypeConverter;
import  dev.bxlab.resultset_mapper.core.ColumnMapping;
import  dev.bxlab.resultset_mapper.utils.ConverterUtils;
import  dev.bxlab.resultset_mapper.utils.ReflectionUtils;
import  dev.bxlab.resultset_mapper.utils.ValueUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class FieldConfig {
    public static final String FORMAT_ATTRIBUTE = "format";

    private final String columnName;
    private final TypeConverter<?> converter;
    private final Map<String, Object> attributes;

    private FieldConfig(FieldConfigBuilder builder) {
        this.columnName = builder.columnName;
        this.converter = builder.converter;
        this.attributes = builder.attributes;
    }

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

    public static FieldConfigBuilder builder() {
        return new FieldConfigBuilder();
    }

    public Optional<String> getColumnName() {
        return Optional.ofNullable(this.columnName);
    }

    public Optional<TypeConverter<?>> getConverter() {
        return Optional.ofNullable(this.converter);
    }

    public Map<String, Object> getAttributes() {
        return this.attributes == null ? new HashMap<>() : attributes;
    }

    public <T> Optional<T> getAttribute(String key, Class<T> type) {
        Object value = this.attributes.get(key);
        return ValueUtils.isEmpty(value) ? Optional.empty() : Optional.of(type.cast(value));
    }

    public static class FieldConfigBuilder {
        private final Map<String, Object> attributes;

        private String columnName;
        private TypeConverter<?> converter;

        public FieldConfigBuilder() {
            this.attributes = new HashMap<>();
        }

        public FieldConfigBuilder toColumn(String columnName) {
            this.columnName = columnName;
            return this;
        }

        public FieldConfigBuilder withConverter(TypeConverter<?> converter) {
            this.converter = converter;
            return this;
        }

        public FieldConfigBuilder withAttribute(String key, Object value) {
            this.attributes.put(key, value);
            return this;
        }

        public FieldConfigBuilder withAttributes(Map<String, Object> attributes) {
            this.attributes.putAll(ValueUtils.requireNonNull(attributes, "Attributes can not be null"));
            return this;
        }

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
