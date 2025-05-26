package dev.bxlab.resultset.mapper.core;

import dev.bxlab.resultset.mapper.configs.FieldConfig;
import dev.bxlab.resultset.mapper.configs.MapperConfig;
import dev.bxlab.resultset.mapper.converters.ConverterRegistry;
import dev.bxlab.resultset.mapper.converters.StandardConverters;
import dev.bxlab.resultset.mapper.converters.TypeConverter;
import dev.bxlab.resultset.mapper.exceptions.ColumnNotFoundException;
import dev.bxlab.resultset.mapper.exceptions.ObjectInstantiationException;
import dev.bxlab.resultset.mapper.utils.ExceptionHandler;
import dev.bxlab.resultset.mapper.utils.ReflectionUtils;

import java.lang.reflect.Field;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Maps rows from a {@link ResultSet} to objects of type {@code T}.
 * This class handles the conversion of data based on field configurations and type converters.
 *
 * @param <T> the type of object to map the ResultSet rows to
 */
public class RowMapper<T> implements ResultSetMapper<T> {
    private final Class<T> targetType;
    private final MapperConfig mapperConfig;
    private final Map<Field, FieldConfig> mappings;
    private final ConverterRegistry converterRegistry;

    /**
     * Constructs a RowMapper instance using a {@link RowMapperBuilder}.
     *
     * @param builder the builder instance containing the mapping configurations
     */
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

    /**
     * Maps the current row of the given {@link ResultSet} to an object of type {@code T}.
     *
     * @param resultSet the ResultSet to map from, positioned at the row to be mapped
     * @return an object of type {@code T} populated with data from the current ResultSet row
     * @throws SQLException if a database access error occurs or this method is called on a closed result set
     */
    @Override
    public T map(ResultSet resultSet) throws SQLException {
        Map<Field, Object> fieldValues = new HashMap<>();
        Map<String, String> availableColumns = this.getAvailableColumns(resultSet);

        for (Map.Entry<Field, FieldConfig> entry : this.mappings.entrySet()) {
            FieldConfig fieldConfig = entry.getValue();

            // Column name definition
            String lookupName = fieldConfig.getColumnName().orElseThrow();

            Optional<String> columnName = this.findColumnName(availableColumns, lookupName);

            if (columnName.isEmpty()) {
                if (this.mapperConfig.isIgnoreUnknownColumns()) continue;
                throw new ColumnNotFoundException(lookupName);
            }

            Optional<TypeConverter<?>> converter = fieldConfig.getConverter();
            if (converter.isEmpty()) {
                if (this.mapperConfig.isIgnoreUnknownTypes()) continue;
                converter = Optional.of(StandardConverters.OBJECT);
            }

            Field field = entry.getKey();
            Object value = converter.get().convert(resultSet, columnName.get(), fieldConfig.getAttributes());
            if (value != null || !ReflectionUtils.isPrimitiveType(field)) {
                fieldValues.put(field, value);
            }
        }

        return ExceptionHandler.map(() -> ReflectionUtils.createInstanceWithValues(this.targetType, fieldValues),
                (e) -> new ObjectInstantiationException(this.targetType, e));
    }

    /**
     * Retrieves a map of available column names from the ResultSet.
     * The map keys are lookup names (potentially case-insensitive), and values are actual column names.
     *
     * @param rs the ResultSet to extract column names from
     * @return a map of lookup names to actual column names
     * @throws SQLException if a database access error occurs
     */
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

    /**
     * Finds the actual column name in the map of available columns based on the lookup name.
     * Considers case-insensitivity based on mapper configuration.
     *
     * @param columns    a map of available column names (lookup name -> actual name)
     * @param lookupName the name to look up
     * @return an Optional containing the actual column name if found, otherwise an empty Optional
     */
    private Optional<String> findColumnName(Map<String, String> columns, String lookupName) {
        if (this.mapperConfig.isCaseInsensitiveColumns()) {
            return Optional.ofNullable(columns.get(lookupName.toLowerCase()));
        }
        return Optional.ofNullable(columns.get(lookupName));
    }

    /**
     * Initializes the field mappings for the target type.
     * It inspects fields annotated with {@link ColumnMapping} and creates corresponding {@link FieldConfig} instances.
     * Configuration priority is: mapper-level config > annotation config > default naming strategy.
     *
     * @throws ObjectInstantiationException if a converter specified in an annotation cannot be instantiated
     */
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
                throw new ObjectInstantiationException(mappingAnnotation.converter(), e);
            }
        }
    }
}
