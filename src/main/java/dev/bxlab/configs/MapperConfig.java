package dev.bxlab.configs;

import dev.bxlab.core.RowMapperBuilder;
import dev.bxlab.utils.ValueUtils;

import java.util.Map;
import java.util.Optional;

public class MapperConfig {

    private final boolean ignoreUnknowTypes;
    private final boolean ignoreUnknownColumns;
    private final boolean caseInsensitiveColumns;
    private final NamingStrategy namingStrategy;
    private final Map<String, FieldConfig> fieldMappingConfigs;

    public MapperConfig(RowMapperBuilder<?> builder) {
        this.ignoreUnknowTypes = builder.isIgnoreUnknowTypes();
        this.ignoreUnknownColumns = builder.isIgnoreUnknownColumns();
        this.caseInsensitiveColumns = builder.isCaseInsensitiveColumns();
        this.namingStrategy = ValueUtils.requireNonNull(builder.getNamingStrategy(), "Naming strategy can not be null");
        this.fieldMappingConfigs = ValueUtils.requireNonNull(builder.getFieldConfigs(), "Field mapping configurations can not be null");
    }

    public Optional<FieldConfig> getFieldConfig(String fieldName) {
        if (this.fieldMappingConfigs == null) return Optional.empty();
        return Optional.ofNullable(this.fieldMappingConfigs.get(fieldName));
    }

    public boolean isIgnoreUnknowTypes() {
        return ignoreUnknowTypes;
    }

    public boolean isIgnoreUnknownColumns() {
        return ignoreUnknownColumns;
    }

    public boolean isCaseInsensitiveColumns() {
        return caseInsensitiveColumns;
    }

    public NamingStrategy getNamingStrategy() {
        return namingStrategy;
    }
}
