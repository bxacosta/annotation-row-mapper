package dev.bxlab.converters;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ConverterRegistry {

    private final Map<Class<?>, TypeConverter<?>> converters = new ConcurrentHashMap<>();

    public static ConverterRegistry withDefaults() {
        ConverterRegistry registry = new ConverterRegistry();
        BasicConverters.registerDefaults(registry);
        return registry;
    }

    public void register(Class<?> type, TypeConverter<?> converter) {
        this.converters.put(type, converter);
    }

    public Optional<TypeConverter<?>> lockup(Class<?> type) {
        // Check for exact match
        TypeConverter<?> converter = converters.get(type);
        if (converter != null) return Optional.of(converter);

        // Check for assignable match
        for (Map.Entry<Class<?>, TypeConverter<?>> converterEntry : this.converters.entrySet()) {
            Class<?> converterType = converterEntry.getKey();

            if (converterType.isAssignableFrom(type))
                return Optional.ofNullable(converterEntry.getValue());
        }

        return Optional.empty();
    }

    public Map<Class<?>, TypeConverter<?>> getConverters() {
        return converters;
    }
}
