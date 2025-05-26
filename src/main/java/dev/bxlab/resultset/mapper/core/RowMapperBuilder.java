package dev.bxlab.resultset.mapper.core;

import dev.bxlab.resultset.mapper.configs.FieldConfig;
import dev.bxlab.resultset.mapper.configs.NamingStrategy;
import dev.bxlab.resultset.mapper.converters.TypeConverter;
import dev.bxlab.resultset.mapper.utils.ValueUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * Builder class for creating and configuring {@link RowMapper} instances.
 * Provides a fluent API to define mapping strategies, field configurations, and custom type converters.
 *
 * @param <T> the type of object the RowMapper will produce
 */
public class RowMapperBuilder<T> {
    private final Class<T> targetType;
    private final Map<String, FieldConfig> fieldConfigs;
    private final Map<Class<?>, TypeConverter<?>> converters;

    private NamingStrategy namingStrategy;
    private boolean ignoreUnknowTypes;
    private boolean ignoreUnknownColumns;
    private boolean caseInsensitiveColumns;
    private boolean includeDefaultConverters;

    /**
     * Private constructor to initialize the builder for a specific target type.
     *
     * @param targetType the class of the object to be mapped
     */
    private RowMapperBuilder(Class<T> targetType) {
        this.targetType = targetType;
        this.fieldConfigs = new HashMap<>();
        this.converters = new HashMap<>();

        this.namingStrategy = NamingStrategy.AS_IS;
        this.ignoreUnknowTypes = true;
        this.ignoreUnknownColumns = true;
        this.caseInsensitiveColumns = true;
        this.includeDefaultConverters = true;
    }

    /**
     * Creates a new RowMapperBuilder for the specified target type.
     *
     * @param targetType the class of the object to be mapped
     * @param <T>        the type of the object
     * @return a new instance of RowMapperBuilder
     */
    public static <T> RowMapperBuilder<T> forType(Class<T> targetType) {
        return new RowMapperBuilder<>(targetType);
    }

    /**
     * Gets the target type for which the mapper is being built.
     *
     * @return the target class
     */
    public Class<T> getTargetType() {
        return this.targetType;
    }

    /**
     * Gets the configured field configurations.
     *
     * @return a map of field names to their {@link FieldConfig}
     */
    public Map<String, FieldConfig> getFieldConfigs() {
        return this.fieldConfigs;
    }

    /**
     * Gets the registered custom type converters.
     *
     * @return a map of types to their {@link TypeConverter}
     */
    public Map<Class<?>, TypeConverter<?>> getConverters() {
        return this.converters;
    }

    /**
     * Gets the currently configured naming strategy.
     *
     * @return the {@link NamingStrategy}
     */
    public NamingStrategy getNamingStrategy() {
        return this.namingStrategy;
    }

    /**
     * Checks if unknown types should be ignored during mapping.
     *
     * @return true if unknown types are ignored, false otherwise
     */
    public boolean isIgnoreUnknowTypes() {
        return this.ignoreUnknowTypes;
    }

    /**
     * Checks if unknown columns in the ResultSet should be ignored.
     *
     * @return true if unknown columns are ignored, false otherwise
     */
    public boolean isIgnoreUnknownColumns() {
        return this.ignoreUnknownColumns;
    }

    /**
     * Checks if column name matching should be case-insensitive.
     *
     * @return true if column matching is case-insensitive, false otherwise
     */
    public boolean isCaseInsensitiveColumns() {
        return this.caseInsensitiveColumns;
    }

    /**
     * Checks if default type converters should be included.
     *
     * @return true if default converters are included, false otherwise
     */
    public boolean isIncludeDefaultConverters() {
        return this.includeDefaultConverters;
    }

    /**
     * Sets the naming strategy for converting field names to column names.
     *
     * @param strategy the {@link NamingStrategy} to use
     * @return this builder instance for fluent chaining
     */
    public RowMapperBuilder<T> withNamingStrategy(NamingStrategy strategy) {
        this.namingStrategy = strategy;
        return this;
    }

    /**
     * Configures whether to ignore types for which no converter is found.
     *
     * @param ignore true to ignore unknown types, false to throw an exception
     * @return this builder instance for fluent chaining
     */
    public RowMapperBuilder<T> ignoreUnknownTypes(boolean ignore) {
        this.ignoreUnknowTypes = ignore;
        return this;
    }

    /**
     * Configures whether to ignore columns in the ResultSet that are not mapped to any field.
     *
     * @param ignore true to ignore unknown columns, false to throw an exception
     * @return this builder instance for fluent chaining
     */
    public RowMapperBuilder<T> ignoreUnknownColumns(boolean ignore) {
        this.ignoreUnknownColumns = ignore;
        return this;
    }

    /**
     * Configures whether column name matching should be case-insensitive.
     *
     * @param caseInsensitive true for case-insensitive matching, false for case-sensitive
     * @return this builder instance for fluent chaining
     */
    public RowMapperBuilder<T> caseInsensitiveColumns(boolean caseInsensitive) {
        this.caseInsensitiveColumns = caseInsensitive;
        return this;
    }

    /**
     * Configures whether to include the set of default type converters.
     *
     * @param include true to include default converters, false to use only registered ones
     * @return this builder instance for fluent chaining
     */
    public RowMapperBuilder<T> includeDefaultConverters(boolean include) {
        this.includeDefaultConverters = include;
        return this;
    }

    /**
     * Configures mapping for a specific field.
     *
     * @param fieldName  the name of the field to configure
     * @param configurer a consumer that accepts a {@link FieldConfig.FieldConfigBuilder} to define the field's mapping
     * @return this builder instance for fluent chaining
     */
    public RowMapperBuilder<T> mapField(String fieldName, Consumer<FieldConfig.FieldConfigBuilder> configurer) {
        FieldConfig.FieldConfigBuilder builder = new FieldConfig.FieldConfigBuilder();
        configurer.accept(builder);
        this.fieldConfigs.put(fieldName, builder.build());
        return this;
    }

    /**
     * Registers a custom type converter for a specific type.
     *
     * @param type      the class of the type this converter handles
     * @param converter the {@link TypeConverter} instance
     * @param <U>       the type handled by the converter
     * @return this builder instance for fluent chaining
     */
    public <U> RowMapperBuilder<T> registerConverter(Class<U> type, TypeConverter<U> converter) {
        this.converters.put(type, converter);
        return this;
    }

    /**
     * Builds the {@link ResultSetMapper} (specifically a {@link RowMapper}) instance
     * based on the current configuration.
     * Validates that essential configurations like target type and naming strategy are set.
     *
     * @return a new {@link ResultSetMapper} instance
     * @throws IllegalArgumentException if required configurations are missing or invalid
     */
    public ResultSetMapper<T> build() {
        ValueUtils.requireNonNull(this.targetType, "Target type can not be null");
        ValueUtils.requireNonNull(this.namingStrategy, "Naming strategy can not be null");

        this.fieldConfigs.forEach((key, value) -> {
            ValueUtils.requireNonEmpty(key, "Field name can not be empty");
            ValueUtils.requireNonNull(value, "Field config value can not be null");
        });

        this.converters.forEach((key, value) -> {
            ValueUtils.requireNonNull(key, "Converter type can not be null");
            ValueUtils.requireNonNull(value, "Converter value can not be null");
        });

        return new RowMapper<>(this);
    }
}
