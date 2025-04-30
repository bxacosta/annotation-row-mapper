package dev.bxlab.core;

import java.lang.reflect.Field;

public record MappingContext(Field field, String format, String columnName) {
}
