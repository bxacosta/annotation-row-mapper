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
        if (value.isEmpty() || value.length() == 1) return value.toLowerCase();

        StringBuilder builder = new StringBuilder();

        for (int i = 0; i < value.length(); i++) {
            char character = value.charAt(i);
            if (Character.isUpperCase(character)) {
                builder.append("_").append(Character.toLowerCase(character));
            } else {
                builder.append(character);
            }
        }

        return builder.toString();
    }

    public String fieldToColumnName(String fieldName) {
        return converter.apply(fieldName);
    }
}
