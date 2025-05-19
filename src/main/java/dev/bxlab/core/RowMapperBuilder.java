package dev.bxlab.core;

import dev.bxlab.configs.FieldConfig;
import dev.bxlab.configs.NamingStrategy;
import dev.bxlab.converters.TypeConverter;
import dev.bxlab.utils.ValueUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class RowMapperBuilder<T> {
    private final Class<T> targetType;
    private final Map<String, FieldConfig> fieldConfigs;
    private final Map<Class<?>, TypeConverter<?>> converters;

    private NamingStrategy namingStrategy;
    private boolean ignoreUnknowTypes;
    private boolean ignoreUnknownColumns;
    private boolean caseInsensitiveColumns;
    private boolean includeDefaultConverters;

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

    public static <T> RowMapperBuilder<T> forType(Class<T> targetType) {
        return new RowMapperBuilder<>(targetType);
    }

    public Class<T> getTargetType() {
        return this.targetType;
    }

    public Map<String, FieldConfig> getFieldConfigs() {
        return this.fieldConfigs;
    }

    public Map<Class<?>, TypeConverter<?>> getConverters() {
        return this.converters;
    }

    public NamingStrategy getNamingStrategy() {
        return this.namingStrategy;
    }

    public boolean isIgnoreUnknowTypes() {
        return this.ignoreUnknowTypes;
    }

    public boolean isIgnoreUnknownColumns() {
        return this.ignoreUnknownColumns;
    }

    public boolean isCaseInsensitiveColumns() {
        return this.caseInsensitiveColumns;
    }

    public boolean isIncludeDefaultConverters() {
        return this.includeDefaultConverters;
    }

    public RowMapperBuilder<T> withNamingStrategy(NamingStrategy strategy) {
        this.namingStrategy = strategy;
        return this;
    }

    public RowMapperBuilder<T> ignoreUnknownTypes(boolean ignore) {
        this.ignoreUnknowTypes = ignore;
        return this;
    }

    public RowMapperBuilder<T> ignoreUnknownColumns(boolean ignore) {
        this.ignoreUnknownColumns = ignore;
        return this;
    }

    public RowMapperBuilder<T> caseInsensitiveColumns(boolean caseInsensitive) {
        this.caseInsensitiveColumns = caseInsensitive;
        return this;
    }

    public RowMapperBuilder<T> includeDefaultConverters(boolean include) {
        this.includeDefaultConverters = include;
        return this;
    }

    public RowMapperBuilder<T> mapField(String fieldName, Consumer<FieldConfig.FieldConfigBuilder> configurer) {
        FieldConfig.FieldConfigBuilder builder = new FieldConfig.FieldConfigBuilder();
        configurer.accept(builder);
        this.fieldConfigs.put(fieldName, builder.build());
        return this;
    }

    public <U> RowMapperBuilder<T> registerConverter(Class<U> type, TypeConverter<U> converter) {
        this.converters.put(type, converter);
        return this;
    }

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
