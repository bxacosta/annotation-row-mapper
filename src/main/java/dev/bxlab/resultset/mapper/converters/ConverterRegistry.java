package dev.bxlab.resultset.mapper.converters;

import dev.bxlab.resultset.mapper.utils.ValueUtils;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Registry for type converters that transform ResultSet column values into Java objects.
 * <p>
 * This class manages a collection of {@link TypeConverter} instances mapped to their target types.
 * It provides methods to register, lookup, and retrieve converters for specific Java types.
 * </p>
 */
public class ConverterRegistry {

    private final Map<Class<?>, TypeConverter<?>> converters;

    /**
     * Creates a new ConverterRegistry with default converters registered.
     */
    public ConverterRegistry() {
        this.converters = new ConcurrentHashMap<>();
    }

    /**
     * Creates a new ConverterRegistry with all standard type converters pre-registered.

     * @return A new ConverterRegistry with standard converters registered
     */
    public static ConverterRegistry withDefaults() {
        ConverterRegistry registry = new ConverterRegistry();
        StandardConverters.registerDefaults(registry);
        return registry;
    }

    /**
     * Registers a type converter for a specific Java class.
     *
     * @param type The Java class that this converter handles
     * @param converter The converter implementation to use for the specified type
     */
    public void register(Class<?> type, TypeConverter<?> converter) {
        this.converters.put(type, converter);
    }

    /**
     * Registers multiple type converters at once.
     *
     * @param converters A map of Java classes with their corresponding type converters
     * @throws NullPointerException if the converter map is null
     */
    public void registerAll(Map<Class<?>, TypeConverter<?>> converters) {
        this.converters.putAll(ValueUtils.requireNonNull(converters, "Converters can not be null"));
    }

    /**
     * Looks up a converter for the specified type.
     * <p>
     * This method first tries to find an exact match for the given type. If no exact match is found,
     * it searches for a converter that can handle a superclass or interface of the given type.
     * </p>
     *
     * @param type The Java class to find a converter for
     * @return An Optional containing the converter if found, or empty if no suitable converter exists
     * @throws NullPointerException if the type is null
     */
    public Optional<TypeConverter<?>> lockup(Class<?> type) {
        ValueUtils.requireNonNull(type, "Type can not be null");

        // Check for the exact match
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

    /**
     * Returns the map of all registered type converters.
     *
     * @return A map of Java classes with their corresponding type converters
     */
    public Map<Class<?>, TypeConverter<?>> getConverters() {
        return converters;
    }
}
