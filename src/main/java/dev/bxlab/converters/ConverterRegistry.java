package dev.bxlab.converters;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ConverterRegistry {

    private final Map<Class<?>, TypeConverter<?>> converters = new ConcurrentHashMap<>();

    public static ConverterRegistry withDefaults() {
        ConverterRegistry registry = new ConverterRegistry();
        DefaultConverters.registerDefaults(registry);
        return registry;
    }

    public <T> void register(Class<T> type, TypeConverter<T> converter) {
        this.converters.put(type, converter);
    }

    @SuppressWarnings("unchecked")
    public <T> Optional<TypeConverter<T>> lockup(Class<T> type) {
        // Check for exact match
        TypeConverter<?> converter = converters.get(type);
        if (converter != null) return Optional.of((TypeConverter<T>) converter);

        // Check for assignable match
        for (Map.Entry<Class<?>, TypeConverter<?>> converterEntry : this.converters.entrySet()) {
            Class<?> converterType = converterEntry.getKey();

            if (converterType.isAssignableFrom(type))
                return Optional.ofNullable((TypeConverter<T>) converterEntry.getValue());
        }

        return Optional.empty();
    }
}
