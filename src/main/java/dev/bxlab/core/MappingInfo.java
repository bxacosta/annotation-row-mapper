package dev.bxlab.core;

import java.lang.reflect.Field;

public record MappingInfo(Field field, String format, String columnName) {
}
