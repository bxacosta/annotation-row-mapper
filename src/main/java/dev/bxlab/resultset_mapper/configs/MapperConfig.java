package dev.bxlab.resultset_mapper.configs;

import dev.bxlab.resultset_mapper.core.RowMapperBuilder;
import dev.bxlab.resultset_mapper.utils.ValueUtils;

import java.util.Map;
import java.util.Optional;

/**
 * This class holds the configuration settings that control how ResultSet data
 * is mapped to Java objects, including naming strategies and field mappings.
 */
public class MapperConfig {

    private final boolean ignoreUnknownTypes;
    private final boolean ignoreUnknownColumns;
    private final boolean caseInsensitiveColumns;
    private final dev.bxlab.resultset_mapper.configs.NamingStrategy namingStrategy;
    private final Map<String, dev.bxlab.resultset_mapper.configs.FieldConfig> fieldMappingConfigs;

    /**
     * Creates a new mapper configuration from a builder.
     *
     * @param builder The builder containing configuration settings
     */
    public MapperConfig(RowMapperBuilder<?> builder) {
        this.ignoreUnknownTypes = builder.isIgnoreUnknowTypes();
        this.ignoreUnknownColumns = builder.isIgnoreUnknownColumns();
        this.caseInsensitiveColumns = builder.isCaseInsensitiveColumns();
        this.namingStrategy = ValueUtils.requireNonNull(builder.getNamingStrategy(), "Naming strategy can not be null");
        this.fieldMappingConfigs = ValueUtils.requireNonNull(builder.getFieldConfigs(), "Field mapping configurations can not be null");
    }

    /**
     * Gets the field configuration for a specific field.
     *
     * @param fieldName The name of the field
     * @return The field configuration or empty if not found
     */
    public Optional<dev.bxlab.resultset_mapper.configs.FieldConfig> getFieldConfig(String fieldName) {
        if (this.fieldMappingConfigs == null) return Optional.empty();
        return Optional.ofNullable(this.fieldMappingConfigs.get(fieldName));
    }

    /**
     * Checks if unknown types should be ignored during mapping.
     *
     * @return True if unknown types should be ignored
     */
    public boolean isIgnoreUnknownTypes() {
        return ignoreUnknownTypes;
    }

    /**
     * Checks if unknown columns should be ignored during mapping.
     *
     * @return True if unknown columns should be ignored
     */
    public boolean isIgnoreUnknownColumns() {
        return ignoreUnknownColumns;
    }

    /**
     * Checks if column names should be matched case-insensitively.
     *
     * @return True if column names are case-insensitive
     */
    public boolean isCaseInsensitiveColumns() {
        return caseInsensitiveColumns;
    }

    /**
     * Gets the naming strategy used for mapping between field and column names.
     *
     * @return The configured naming strategy
     */
    public dev.bxlab.resultset_mapper.configs.NamingStrategy getNamingStrategy() {
        return namingStrategy;
    }
}
