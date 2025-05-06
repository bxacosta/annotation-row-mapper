package dev.bxlab.core;

import dev.bxlab.configs.FieldConfig;
import dev.bxlab.configs.MapperConfig;
import dev.bxlab.converters.ConverterRegistry;
import dev.bxlab.converters.BasicConverters;
import dev.bxlab.converters.TypeConverter;
import dev.bxlab.utils.ConverterUtils;
import dev.bxlab.utils.ExceptionHandler;
import dev.bxlab.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;

public class DefaultResultSetMapper<T> implements ResultSetMapper<T> {

    private final Class<T> targetType;
    private final MapperConfig mapperConfig;
    private final Map<Field, FieldConfig> mappings;
    private final ConverterRegistry converterRegistry;

    protected DefaultResultSetMapper(MapperBuilder<T> builder) {
        this.targetType = builder.getTargetType();
        this.mapperConfig = new MapperConfig(
                builder.isIgnoreUnknownColumns(),
                builder.isIgnoreMissingConverters(),
                builder.isCaseInsensitiveColumns(),
                builder.getNamingStrategy(),
                builder.getFieldConfigs()
        );
        this.converterRegistry = builder.isIncludeDefaultConverters()
                ? ConverterRegistry.withDefaults()
                : new ConverterRegistry();

        builder.getConverterRegistry().getConverters().forEach(converterRegistry::register);

        this.mappings = new HashMap<>();
        this.initializeMappings();
    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException {
        T targetInstance = ExceptionHandler.map(() -> ReflectionUtils.createInstance(targetType),
                (e) -> new IllegalStateException("Error mapping to: " + targetType.getSimpleName(), e));

        Set<String> availableColumns = this.getAvailableColumns(resultSet);

        for (Map.Entry<Field, FieldConfig> entry : mappings.entrySet()) {
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
            columns.add(metaData.getColumnLabel(i).toLowerCase());
        }

        return columns;
    }

    private void initializeMappings() {
        for (Field field : ReflectionUtils.getAllFields(this.targetType)) {
            ColumnMapping mappingAnnotation = field.getAnnotation(ColumnMapping.class);

            if (mappingAnnotation == null) continue;

            try {
                // TODO: merge fields, prioritizing mapper config
                FieldConfig fieldConfig = this.mapperConfig.getFieldConfig(field).orElse(FieldConfig.from(mappingAnnotation));

                TypeConverter<?> converter = fieldConfig.getConverter()
                        .filter(Predicate.not(ConverterUtils::isDefaultConverter))
                        .or(() -> this.converterRegistry.lockup(field.getType()))
                        .orElse(BasicConverters.OBJECT);

                String columnName = fieldConfig.getColumnName()
                        .orElse(this.mapperConfig.namingStrategy().fieldToColumnName(field.getName()));

                mappings.put(field, FieldConfig.builder()
                        .toColumn(columnName)
                        .withConverter(converter)
                        .withAttributes(fieldConfig.getAttributes())
                        .build());
            } catch (ReflectiveOperationException e) {
                throw new IllegalStateException("Cannot instantiate converter: " + mappingAnnotation.converter(), e);
            }
        }
    }
}
