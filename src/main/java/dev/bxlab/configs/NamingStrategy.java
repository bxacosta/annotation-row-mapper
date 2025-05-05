package dev.bxlab.configs;

import java.util.function.Function;

public enum NamingStrategy {
    AS_IS(name -> name),
    SNAKE_CASE(name -> {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < name.length(); i++) {
            char c = name.charAt(i);
            if (Character.isUpperCase(c)) {
                if (i > 0) result.append('_');
                result.append(Character.toLowerCase(c));
            } else {
                result.append(c);
            }
        }
        return result.toString();
    }),
    UPPER_SNAKE_CASE(name -> SNAKE_CASE.fieldToColumnName(name).toUpperCase());

    private final Function<String, String> converter;

    NamingStrategy(Function<String, String> converter) {
        this.converter = converter;
    }

    public String fieldToColumnName(String fieldName) {
        return converter.apply(fieldName);
    }
}
