package  dev.bxlab.resultset_mapper.utils;

import java.util.Collection;
import java.util.Map;

public final class ValueUtils {

    private ValueUtils() {
    }

    public static <T> T ifEmpty(T value, T other) {
        return isEmpty(value) ? other : value;
    }

    public static boolean isEmpty(Object value) {
        return switch (value) {
            case null -> true;
            case String string -> string.trim().isEmpty();
            case Collection<?> collection -> collection.isEmpty();
            case Map<?, ?> map -> map.isEmpty();
            default -> false;
        };
    }

    public static <T> T requireNonEmpty(T value, String message) {
        if (isEmpty(value)) throw new IllegalArgumentException(message);
        return value;
    }

    public static <T> T requireNonNull(T value, String message) {
        if (value == null) throw new IllegalArgumentException(message);
        return value;
    }
}
