package dev.bxlab.core;

import dev.bxlab.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AnnotationBasedRowMapper<T> implements ResultSetMapper<T> {

    private final Class<T> targetClass;
    private final Map<String, FieldInfo> mappings;
    private final Map<Class<? extends ValueConverter<?>>, ValueConverter<?>> converterCache = new HashMap<>();

    public AnnotationBasedRowMapper(Class<T> targetClass) {
        this.targetClass = targetClass;
        this.mappings = new HashMap<>();
        initializeMappings();
    }

    @Override
    public T mapRow(ResultSet resultSet) throws SQLException {
        try {
            T instance = ReflectionUtils.createInstance(targetClass);
            Set<String> availableColumns = this.getAvailableColumns(resultSet);

            for (Map.Entry<String, FieldInfo> entry : mappings.entrySet()) {
                String columnName = entry.getKey();

                if (!availableColumns.contains(columnName.toLowerCase())) continue;

                FieldInfo fieldInfo = entry.getValue();
                MappingInfo mappingInfo = new MappingInfo(fieldInfo.field, fieldInfo.format, columnName);
                Object value = fieldInfo.converter.convert(resultSet, mappingInfo);

                if (value != null || !fieldInfo.isPrimitive) {
                    try {
                        ReflectionUtils.setFieldValue(instance, fieldInfo.field, value);
                    } catch (ReflectiveOperationException e) {
                        throw new SQLException("Error setting field value", e);
                    }
                }
            }
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new SQLException("Error mapping to: " + targetClass.getSimpleName(), e);
        }
    }

    private void initializeMappings() {
        for (Field field : ReflectionUtils.getAllFields(targetClass)) {
            ColumnMapping annotation = field.getAnnotation(ColumnMapping.class);

            if (annotation == null) continue;

            Class<? extends ValueConverter<?>> converterClass = annotation.converter();
            ValueConverter<?> converter = converterCache.computeIfAbsent(
                    converterClass,
                    clazz -> {
                        try {
                            return ReflectionUtils.createInstance(clazz);
                        } catch (ReflectiveOperationException e) {
                            throw new IllegalStateException("Cannot instantiate converter: " + clazz, e);
                        }
                    }
            );

            String columnName = annotation.value();
            String format = annotation.format();
            boolean isPrimitive = ReflectionUtils.isPrimitiveType(field);
            mappings.put(columnName, new FieldInfo(field, format, isPrimitive, converter));
        }
    }

    private Set<String> getAvailableColumns(ResultSet rs) throws SQLException {
        Set<String> columns = new HashSet<>();
        ResultSetMetaData metaData = rs.getMetaData();
        int columnCount = metaData.getColumnCount();

        for (int i = 1; i <= columnCount; i++) {
            columns.add(metaData.getColumnLabel(i).toLowerCase());
        }

        return columns;
    }

    private record FieldInfo(
            Field field,
            String format,
            boolean isPrimitive,
            ValueConverter<?> converter) {
    }
}