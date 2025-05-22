package  dev.bxlab.resultset_mapper.converters;

import  dev.bxlab.resultset_mapper.utils.ValueUtils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

public class ConverterRegistry {

    private final Map<Class<?>,  dev.bxlab.resultset_mapper.converters.TypeConverter<?>> converters;

    public ConverterRegistry() {
        this.converters = new ConcurrentHashMap<>();
    }

    public ConverterRegistry(Map<Class<?>,  dev.bxlab.resultset_mapper.converters.TypeConverter<?>> converters) {
        this.converters = ValueUtils.requireNonNull(converters, "Converters can not be null");
    }

    public static ConverterRegistry withDefaults() {
        ConverterRegistry registry = new ConverterRegistry();
         dev.bxlab.resultset_mapper.converters.StandardConverters.registerDefaults(registry);
        return registry;
    }

    public void register(Class<?> type,  dev.bxlab.resultset_mapper.converters.TypeConverter<?> converter) {
        this.converters.put(type, converter);
    }

    public void registerAll(Map<Class<?>,  dev.bxlab.resultset_mapper.converters.TypeConverter<?>> converters) {
        this.converters.putAll(ValueUtils.requireNonNull(converters, "Converters can not be null"));
    }

    public Optional< dev.bxlab.resultset_mapper.converters.TypeConverter<?>> lockup(Class<?> type) {
        ValueUtils.requireNonNull(type, "Type can not be null");

        // Check for the exact match
         dev.bxlab.resultset_mapper.converters.TypeConverter<?> converter = converters.get(type);
        if (converter != null) return Optional.of(converter);

        // Check for assignable match
        for (Map.Entry<Class<?>,  dev.bxlab.resultset_mapper.converters.TypeConverter<?>> converterEntry : this.converters.entrySet()) {
            Class<?> converterType = converterEntry.getKey();

            if (converterType.isAssignableFrom(type))
                return Optional.ofNullable(converterEntry.getValue());
        }

        return Optional.empty();
    }

    public Map<Class<?>,  dev.bxlab.resultset_mapper.converters.TypeConverter<?>> getConverters() {
        return converters;
    }
}
