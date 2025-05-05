package dev.bxlab.configs;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public record MapperConfig(
        boolean ignoreUnknownColumns,
        boolean ignoreMissingConverter,
        boolean caseInsensitiveColumns,
        NamingStrategy namingStrategy,
        Map<String, FieldConfig> fieldMappingConfigs
) {
    public static MapperConfig withDefaults() {
        return new MapperConfig(
                true,
                false,
                true,
                NamingStrategy.AS_IS,
                new HashMap<>()
        );
    }

    public Optional<FieldConfig> getFieldConfig(Field field) {
        if (fieldMappingConfigs == null) return Optional.empty();

        return Optional.ofNullable(fieldMappingConfigs.get(field.getName()));
    }
}
