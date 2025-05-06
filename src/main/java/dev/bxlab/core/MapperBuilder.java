package dev.bxlab.core;

import dev.bxlab.configs.FieldConfig;
import dev.bxlab.configs.NamingStrategy;
import dev.bxlab.converters.ConverterRegistry;
import dev.bxlab.converters.TypeConverter;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class MapperBuilder<T> {
    private final Class<T> targetType;
    private final Map<String, FieldConfig> fieldConfigs;
    private final ConverterRegistry converterRegistry;

    private NamingStrategy namingStrategy;
    private boolean ignoreUnknownColumns;
    private boolean caseInsensitiveColumns;
    private boolean ignoreMissingConverters;
    private boolean includeDefaultConverters;

    private MapperBuilder(Class<T> targetType) {
        this.targetType = targetType;
        this.fieldConfigs = new HashMap<>();
        this.converterRegistry = new ConverterRegistry();

        this.namingStrategy = NamingStrategy.AS_IS;
        this.ignoreUnknownColumns = true;
        this.ignoreMissingConverters = true;
        this.caseInsensitiveColumns = true;
        this.includeDefaultConverters = true;
    }

    public static <T> MapperBuilder<T> forType(Class<T> targetType) {
        return new MapperBuilder<>(targetType);
    }

    public Class<T> getTargetType() {
        return this.targetType;
    }

    public Map<String, FieldConfig> getFieldConfigs() {
        return this.fieldConfigs;
    }

    public ConverterRegistry getConverterRegistry() {
        return this.converterRegistry;
    }

    public NamingStrategy getNamingStrategy() {
        return this.namingStrategy;
    }

    public boolean isIgnoreUnknownColumns() {
        return this.ignoreUnknownColumns;
    }

    public boolean isCaseInsensitiveColumns() {
        return this.caseInsensitiveColumns;
    }

    public boolean isIgnoreMissingConverters() {
        return this.ignoreMissingConverters;
    }

    public boolean isIncludeDefaultConverters() {
        return this.includeDefaultConverters;
    }

    public MapperBuilder<T> withNamingStrategy(NamingStrategy strategy) {
        this.namingStrategy = strategy;
        return this;
    }

    public MapperBuilder<T> ignoreUnknownColumns(boolean ignore) {
        this.ignoreUnknownColumns = ignore;
        return this;
    }

    public MapperBuilder<T> ignoreMissingConverters(boolean ignore) {
        this.ignoreMissingConverters = ignore;
        return this;
    }

    public MapperBuilder<T> caseInsensitiveColumns(boolean caseInsensitive) {
        this.caseInsensitiveColumns = caseInsensitive;
        return this;
    }

    public MapperBuilder<T> includeDefaultConverters(boolean include) {
        this.includeDefaultConverters = include;
        return this;
    }

    public MapperBuilder<T> mapField(String fieldName, Consumer<FieldConfig.FieldConfigBuilder> configurer) {
        FieldConfig.FieldConfigBuilder builder = new FieldConfig.FieldConfigBuilder();
        configurer.accept(builder);
        this.fieldConfigs.put(fieldName, builder.build());
        return this;
    }

    public <V> MapperBuilder<T> registerConverter(Class<V> type, TypeConverter<V> converter) {
        this.converterRegistry.register(type, converter);
        return this;
    }

    public ResultSetMapper<T> build() {
        return new DefaultResultSetMapper<>(this);
    }
}
