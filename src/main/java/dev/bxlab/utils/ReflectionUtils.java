package dev.bxlab.utils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            Collections.addAll(fields, currentClass.getDeclaredFields());
            currentClass = currentClass.getSuperclass();
        }
        return fields;
    }

    public static <T> T createInstance(Class<T> clazz) throws ReflectiveOperationException {
        return clazz.getDeclaredConstructor().newInstance();
    }

    public static void setFieldValue(Object instance, Field field, Object value) throws ReflectiveOperationException {
        field.setAccessible(true);
        field.set(instance, value);
    }

    public static boolean isPrimitiveType(Field field) {
        return field.getType().isPrimitive();
    }
}
