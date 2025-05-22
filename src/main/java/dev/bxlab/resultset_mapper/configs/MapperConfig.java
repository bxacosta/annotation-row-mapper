package  dev.bxlab.resultset_mapper.configs;

import  dev.bxlab.resultset_mapper.core.RowMapperBuilder;
import  dev.bxlab.resultset_mapper.utils.ValueUtils;

import java.util.Map;
import java.util.Optional;

public class MapperConfig {

    private final boolean ignoreUnknowTypes;
    private final boolean ignoreUnknownColumns;
    private final boolean caseInsensitiveColumns;
    private final  dev.bxlab.resultset_mapper.configs.NamingStrategy namingStrategy;
    private final Map<String,  dev.bxlab.resultset_mapper.configs.FieldConfig> fieldMappingConfigs;

    public MapperConfig(RowMapperBuilder<?> builder) {
        this.ignoreUnknowTypes = builder.isIgnoreUnknowTypes();
        this.ignoreUnknownColumns = builder.isIgnoreUnknownColumns();
        this.caseInsensitiveColumns = builder.isCaseInsensitiveColumns();
        this.namingStrategy = ValueUtils.requireNonNull(builder.getNamingStrategy(), "Naming strategy can not be null");
        this.fieldMappingConfigs = ValueUtils.requireNonNull(builder.getFieldConfigs(), "Field mapping configurations can not be null");
    }

    public Optional< dev.bxlab.resultset_mapper.configs.FieldConfig> getFieldConfig(String fieldName) {
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

    public  dev.bxlab.resultset_mapper.configs.NamingStrategy getNamingStrategy() {
        return namingStrategy;
    }
}
