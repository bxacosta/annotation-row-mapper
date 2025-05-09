package dev.bxlab.core;

import dev.bxlab.configs.FieldConfig;
import dev.bxlab.configs.MapperConfig;
import dev.bxlab.converters.BasicConverters;
import dev.bxlab.converters.ConverterRegistry;
import dev.bxlab.converters.TypeConverter;
import dev.bxlab.utils.ExceptionHandler;
import dev.bxlab.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class DefaultRowMapper<T> implements ResultSetMapper<T> {

    private final Class<T> targetType;
    private final MapperConfig mapperConfig;
    private final Map<Field, FieldConfig> mappings;
    private final ConverterRegistry converterRegistry;

    protected DefaultRowMapper(RowMapperBuilder<T> builder) {
        this.targetType = builder.getTargetType();
        this.mapperConfig = new MapperConfig(builder);
        this.converterRegistry = builder.isIncludeDefaultConverters()
                ? ConverterRegistry.withDefaults()
                : new ConverterRegistry(builder.getConverterRegistry().getConverters());

        this.mappings = new HashMap<>();
        this.initializeMappings();
    }

    @Override
    public T map(ResultSet resultSet) throws SQLException {
        T targetInstance = ExceptionHandler.map(() -> ReflectionUtils.createInstance(this.targetType),
                (e) -> new IllegalStateException("Error mapping to: " + this.targetType.getSimpleName(), e));

        Set<String> availableColumns = this.getAvailableColumns(resultSet);

        for (Map.Entry<Field, FieldConfig> entry : this.mappings.entrySet()) {
            FieldConfig fieldConfig = entry.getValue();

            if (!availableColumns.contains(fieldConfig.getColumnName().orElseThrow().toLowerCase())) continue;

            Field field = entry.getKey();
            Object value = fieldConfig.getConverter().orElseThrow().convert(resultSet, fieldConfig);
            if (value != null || ReflectionUtils.isPrimitiveType(field)) {
                ExceptionHandler.map(() -> ReflectionUtils.setFieldValue(targetInstance, field, value),
                        (e) -> new IllegalStateException("Error setting value for field: " + field.getName(), e));
            }
        }
        return targetInstance;
    }

    private Set<String> getAvailableColumns(ResultSet rs) throws SQLException {
        Set<String> columns = new HashSet<>();
        ResultSetMetaData metaData = rs.getMetaData();

        for (int i = 1; i <= metaData.getColumnCount(); i++) {
            String columnName = metaData.getColumnLabel(i);
            columns.add(this.mapperConfig.isCaseInsensitiveColumns() ? columnName.toLowerCase() : columnName);
        }

        return columns;
    }

    private void initializeMappings() {
        for (Field field : ReflectionUtils.getAllFields(this.targetType)) {
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

                TypeConverter<?> converter = mapperFieldConfig.flatMap(FieldConfig::getConverter)
                        .or(annotationFieldConfig::getConverter)
                        .or(() -> this.converterRegistry.lockup(field.getType()))
                        .orElse(BasicConverters.OBJECT);

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
