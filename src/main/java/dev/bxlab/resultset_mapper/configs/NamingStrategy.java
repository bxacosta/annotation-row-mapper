package dev.bxlab.resultset_mapper.configs;

import java.util.function.Function;

/**
 * This enum provides different naming conventions that can be used when
 * converting between Java's camelCase field naming and database column naming conventions.
 */
public enum NamingStrategy {
    /**
     * Keeps the field name exactly as it is without any transformation.
     */
    AS_IS(Function.identity()),

    /**
     * Converts camelCase field names to snake_case column names.
     * For example, "userName" becomes "user_name".
     */
    SNAKE_CASE(NamingStrategy::camelToSnake),

    /**
     * Converts camelCase field names to UPPER_SNAKE_CASE column names.
     * For example, "userName" becomes "USER_NAME".
     */
    UPPER_SNAKE_CASE(name -> NamingStrategy.camelToSnake(name).toUpperCase());


    private final Function<String, String> converter;

    /**
     * Creates a naming strategy with the specified converter function.
     *
     * @param converter The function that converts field names to column names
     */
    NamingStrategy(Function<String, String> converter) {
        this.converter = converter;
    }

    /**
     * Converts a camelCase string to snake_case.
     * Inserts underscores before uppercase letters when they are preceded by
     * lowercase letters or followed by lowercase letters.
     *
     * @param value The camelCase string to convert
     * @return The converted snake_case string
     */
    private static String camelToSnake(String value) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            if (Character.isUpperCase(character)) {
                boolean prevIsLower = i > 0 && Character.isLowerCase(value.charAt(i - 1));
                boolean nextIsLower = i + 1 < value.length() && Character.isLowerCase(value.charAt(i + 1));

                if (i > 0 && (prevIsLower || nextIsLower)) result.append('_');

                result.append(Character.toLowerCase(character));
            } else {
                result.append(character);
            }
        }
        return result.toString();
    }

    /**
     * Converts a Java field name to a database column name according to this naming strategy.
     *
     * @param fieldName The Java field name to convert
     * @return The converted database column name
     */
    public String fieldToColumnName(String fieldName) {
        return this.converter.apply(fieldName);
    }
}
