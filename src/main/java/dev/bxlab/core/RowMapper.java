package dev.bxlab.core;

import dev.bxlab.configs.FieldConfig;
import dev.bxlab.configs.MapperConfig;
import dev.bxlab.converters.ConverterRegistry;
import dev.bxlab.converters.StandardConverters;
import dev.bxlab.converters.TypeConverter;
import dev.bxlab.utils.ExceptionHandler;
import dev.bxlab.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class RowMapper<T> implements ResultSetMapper<T> {
    private final Class<T> targetType;
    private final MapperConfig mapperConfig;
    private final Map<Field, FieldConfig> mappings;
    private final ConverterRegistry converterRegistry;

    protected RowMapper(RowMapperBuilder<T> builder) {
        this.targetType = builder.getTargetType();
        this.mapperConfig = new MapperConfig(builder);

        ConverterRegistry registry = builder.isIncludeDefaultConverters()
                ? ConverterRegistry.withDefaults()
                : new ConverterRegistry();

        registry.registerAll(builder.getConverters());

        this.converterRegistry = registry;

        this.mappings = new HashMap<>();
        this.initializeMappings();
    }

    @Override
    public T map(ResultSet resultSet) throws SQLException {
        Map<Field, Object> fieldValues = new HashMap<>();
        Map<String, String> availableColumns = this.getAvailableColumns(resultSet);

        for (Map.Entry<Field, FieldConfig> entry : this.mappings.entrySet()) {
            FieldConfig fieldConfig = entry.getValue();

            // Column name definition
            String lookupName = fieldConfig.getColumnName()
                    .orElseThrow(() -> new IllegalStateException("Column name is required"));

            Optional<String> columnName = this.findColumnName(availableColumns, lookupName);

            if (columnName.isEmpty()) {
                if (this.mapperConfig.isIgnoreUnknownColumns()) continue;
                throw new IllegalStateException("Column not found: " + lookupName);
            }

            // Type converter definition
            Optional<TypeConverter<?>> converter = fieldConfig.getConverter();
            if (converter.isEmpty()) {
                if (this.mapperConfig.isIgnoreUnknowTypes()) continue;
                converter = Optional.of(StandardConverters.OBJECT);
            }

            Field field = entry.getKey();
            Object value = converter.get().convert(resultSet, columnName.get(), fieldConfig.getAttributes());
            if (value != null || !ReflectionUtils.isPrimitiveType(field)) {
                fieldValues.put(field, value);
            }
        }

        return ExceptionHandler.map(() -> ReflectionUtils.createInstanceWithValues(this.targetType, fieldValues),
                (e) -> new IllegalStateException("Error mapping to: " + this.targetType.getSimpleName(), e));
    }

    private Map<String, String> getAvailableColumns(ResultSet rs) throws SQLException {
        Map<String, String> columns = new HashMap<>();
        ResultSetMetaData metaData = rs.getMetaData();

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnLabel(i);
            String lookupName = this.mapperConfig.isCaseInsensitiveColumns() ? columnName.toLowerCase() : columnName;
            columns.put(lookupName, columnName);
        }

        return columns;
    }

    private Optional<String> findColumnName(Map<String, String> columns, String lookupName) {
        if (this.mapperConfig.isCaseInsensitiveColumns()) {
            return Optional.ofNullable(columns.get(lookupName.toLowerCase()));
        }
        return Optional.ofNullable(columns.get(lookupName));
    }

    private void initializeMappings() {
        List<Field> fields = ReflectionUtils.getAllFields(this.targetType);

        for (Field field : fields) {
            ColumnMapping mappingAnnotation = field.getAnnotation(ColumnMapping.class);

            if (mappingAnnotation == null) continue;

            try {
                // Determine field config based on priority: mapperConfig > mappingAnnotation > default config strategies
                FieldConfig annotationFieldConfig = FieldConfig.from(mappingAnnotation);
                Optional<FieldConfig> mapperFieldConfig = this.mapperConfig.getFieldConfig(field.getName());

                String columnName = mapperFieldConfig
                        .flatMap(FieldConfig::getColumnName)
                        .or(annotationFieldConfig::getColumnName)
                        .orElse(this.mapperConfig.getNamingStrategy().fieldToColumnName(field.getName()));

                TypeConverter<?> converter = mapperFieldConfig
                        .flatMap(FieldConfig::getConverter)
                        .or(annotationFieldConfig::getConverter)
                        .or(() -> this.converterRegistry.lockup(field.getType()))
                        .orElse(null);

                Map<String, Object> attributes = new HashMap<>(annotationFieldConfig.getAttributes());
                mapperFieldConfig.ifPresent(fieldConfig -> attributes.putAll(fieldConfig.getAttributes()));

                this.mappings.put(field, FieldConfig.builder()
                        .toColumn(columnName)
                        .withConverter(converter)
                        .withAttributes(attributes)
                        .build());
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Cannot instantiate converter: " + mappingAnnotation.converter(), e);
            }
        }
    }
}
