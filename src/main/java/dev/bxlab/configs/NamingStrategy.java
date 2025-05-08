package dev.bxlab.configs;

import java.util.function.Function;

public enum NamingStrategy {
    AS_IS(Function.identity()),
    SNAKE_CASE(NamingStrategy::camelToSnake),
    UPPER_SNAKE_CASE(name -> NamingStrategy.camelToSnake(name).toUpperCase());

    private final Function<String, String> converter;

    NamingStrategy(Function<String, String> converter) {
        this.converter = converter;
    }

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

    public String fieldToColumnName(String fieldName) {
        return converter.apply(fieldName);
    }
}
