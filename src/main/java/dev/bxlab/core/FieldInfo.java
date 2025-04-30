package dev.bxlab.core;

import java.lang.reflect.Field;

public record FieldInfo(
        Field field,
        String format,
        boolean isPrimitive,
        ValueConverter<?> converter
) {
}
