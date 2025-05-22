package  dev.bxlab.resultset_mapper.utils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.RecordComponent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        if (clazz.isRecord()) {
            return createRecordInstance(clazz);
        } else {
            return createClassInstance(clazz);
        }
    }

    public static <T> T createClassInstance(Class<T> clazz) throws ReflectiveOperationException {
        return clazz.getDeclaredConstructor().newInstance();
    }

    public static <T> T createRecordInstance(Class<T> clazz) throws ReflectiveOperationException {
        RecordComponent[] components = clazz.getRecordComponents();

        Class<?>[] paramTypes = Arrays.stream(components)
                .map(RecordComponent::getType)
                .toArray(Class<?>[]::new);

        Constructor<T> constructor = clazz.getDeclaredConstructor(paramTypes);

        Object[] defaultValues = Arrays.stream(components)
                .map(component -> getDefaultValue(component.getType()))
                .toArray();

        return constructor.newInstance(defaultValues);
    }


    public static <T> T createInstanceWithValues(Class<T> clazz, Map<Field, Object> values) throws ReflectiveOperationException {
        if (clazz.isRecord()) {
            return createRecordInstanceWithValues(clazz, values);
        } else {
            return createClassInstanceWithValues(clazz, values);
        }
    }

    public static void setFieldValue(Object instance, Field field, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(instance, value);
    }

    public static boolean isPrimitiveType(Field field) {
        return field.getType().isPrimitive();
    }

    private static <T> T createClassInstanceWithValues(Class<T> clazz, Map<Field, Object> values) throws ReflectiveOperationException {
        T instance = clazz.getDeclaredConstructor().newInstance();
        for (Map.Entry<Field, Object> entry : values.entrySet()) {
            Field field = entry.getKey();
            Object value = entry.getValue();
            setFieldValue(instance, field, value);
        }
        return instance;
    }

    private static <T> T createRecordInstanceWithValues(Class<T> clazz, Map<Field, Object> values) throws ReflectiveOperationException {
        Map<String, Object> valuesByFieldName = new HashMap<>();

        for (Map.Entry<Field, Object> entry : values.entrySet()) {
            valuesByFieldName.put(entry.getKey().getName(), entry.getValue());
        }

        RecordComponent[] components = clazz.getRecordComponents();
        Object[] constructorArgs = new Object[components.length];

        for (int i = 0; i < components.length; i++) {
            RecordComponent component = components[i];
            String fieldName = component.getName();

            if (valuesByFieldName.containsKey(fieldName)) {
                constructorArgs[i] = valuesByFieldName.get(fieldName);
            } else {
                constructorArgs[i] = getDefaultValue(component.getType());
            }
        }

        Constructor<T> constructor = clazz.getDeclaredConstructor(
                Arrays.stream(components).map(RecordComponent::getType).toArray(Class<?>[]::new)
        );

        return constructor.newInstance(constructorArgs);
    }

    private static Object getDefaultValue(Class<?> type) {
        if (type.isPrimitive()) {
            if (type == boolean.class) return false;
            if (type == char.class) return '\u0000';
            if (type == byte.class) return (byte) 0;
            if (type == short.class) return (short) 0;
            if (type == int.class) return 0;
            if (type == long.class) return 0L;
            if (type == float.class) return 0.0f;
            if (type == double.class) return 0.0d;
        }
        return null;
    }
}
