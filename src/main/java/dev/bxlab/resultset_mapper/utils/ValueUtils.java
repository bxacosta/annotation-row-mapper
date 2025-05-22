package  dev.bxlab.resultset_mapper.utils;

import java.util.Collection;
import java.util.Map;

/**
 * Utility class for handling and validating values.
 * Provides methods for checking emptiness and requiring non-null or non-empty values.
 */
public final class ValueUtils {

    private ValueUtils() {
    }

    /**
     * Returns the given value if it's not empty, otherwise returns the other value.
     *
     * @param value the value to check
     * @param other the value to return if the original value is empty
     * @param <T>   the type of the values
     * @return the original value if not empty, otherwise the other value
     */
    public static <T> T ifEmpty(T value, T other) {
        return isEmpty(value) ? other : value;
    }

    /**
     * Checks if the given value is empty.
     * A value is considered empty if it is null, a blank string, an empty collection, or an empty map.
     *
     * @param value the value to check
     * @return true if the value is empty, false otherwise
     */
    public static boolean isEmpty(Object value) {
        return switch (value) {
            case null -> true;
            case String string -> string.trim().isEmpty();
            case Collection<?> collection -> collection.isEmpty();
            case Map<?, ?> map -> map.isEmpty();
            default -> false;
        };
    }

    /**
     * Requires the given value to be non-empty.
     * Throws an IllegalArgumentException with the given message if the value is empty.
     *
     * @param value   the value to check
     * @param message the exception message to use if the value is empty
     * @param <T>     the type of the value
     * @return the original value if not empty
     * @throws IllegalArgumentException if the value is empty
     */
    public static <T> T requireNonEmpty(T value, String message) {
        if (isEmpty(value)) throw new IllegalArgumentException(message);
        return value;
    }

    /**
     * Requires the given value to be non-null.
     * Throws an IllegalArgumentException with the given message if the value is null.
     *
     * @param value   the value to check
     * @param message the exception message to use if the value is null
     * @param <T>     the type of the value
     * @return the original value if not null
     * @throws IllegalArgumentException if the value is null
     */
    public static <T> T requireNonNull(T value, String message) {
        if (value == null) throw new IllegalArgumentException(message);
        return value;
    }
}
