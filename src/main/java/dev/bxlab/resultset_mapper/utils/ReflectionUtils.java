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

/**
 * Utility class for reflection-based operations.
 * Provides methods for instantiating classes (including records), accessing fields, and retrieving class metadata.
 */
public final class ReflectionUtils {

    private ReflectionUtils() {
    }

    /**
     * Gets all fields of a class, including those from its superclasses.
     *
     * @param clazz the class to inspect
     * @return a list of all fields (public, protected, default (package) access, and private) declared by this class or any of its superclasses
     */
    public static List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = clazz;
        while (currentClass != null && currentClass != Object.class) {
            Collections.addAll(fields, currentClass.getDeclaredFields());
            currentClass = currentClass.getSuperclass();
        }
        return fields;
    }

    /**
     * Creates an instance of the given class.
     * Handles both regular classes and records, attempting to use a default or no-arg constructor for classes,
     * and a constructor with default values for record components for records.
     *
     * @param clazz the class to instantiate
     * @param <T>   the type of the class
     * @return a new instance of the class
     * @throws ReflectiveOperationException if an instance cannot be created (e.g., no suitable constructor, access issues)
     */
    public static <T> T createInstance(Class<T> clazz) throws ReflectiveOperationException {
        if (clazz.isRecord()) {
            return createRecordInstance(clazz);
        } else {
            return createClassInstance(clazz);
        }
    }

    /**
     * Creates an instance of a regular (non-record) class using its no-argument constructor.
     *
     * @param clazz the class to instantiate
     * @param <T>   the type of the class
     * @return a new instance of the class
     * @throws ReflectiveOperationException if an instance cannot be created (e.g., no no-arg constructor, access issues)
     */
    public static <T> T createClassInstance(Class<T> clazz) throws ReflectiveOperationException {
        return clazz.getDeclaredConstructor().newInstance();
    }

    /**
     * Creates an instance of a record class using its canonical constructor with default values for components.
     *
     * @param clazz the record class to instantiate
     * @param <T>   the type of the record class
     * @return a new instance of the record class
     * @throws ReflectiveOperationException if an instance cannot be created (e.g., constructor access issues)
     */
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


    /**
     * Creates an instance of the given class and populates its fields with the provided values.
     * Handles both regular classes and records.
     *
     * @param clazz  the class to instantiate
     * @param values a map of fields to their corresponding values
     * @param <T>    the type of the class
     * @return a new instance of the class with fields populated
     * @throws ReflectiveOperationException if an instance cannot be created or fields cannot be set
     */
    public static <T> T createInstanceWithValues(Class<T> clazz, Map<Field, Object> values) throws ReflectiveOperationException {
        if (clazz.isRecord()) {
            return createRecordInstanceWithValues(clazz, values);
        } else {
            return createClassInstanceWithValues(clazz, values);
        }
    }

    /**
     * Sets the value of a field on a given instance.
     * Makes the field accessible if it's not already.
     *
     * @param instance the object whose field is to be set
     * @param field    the field to set
     * @param value    the value to set the field to
     * @throws IllegalAccessException if the field cannot be accessed or set
     */
    public static void setFieldValue(Object instance, Field field, Object value) throws IllegalAccessException {
        field.setAccessible(true);
        field.set(instance, value);
    }

    /**
     * Checks if a field represents a primitive type.
     *
     * @param field the field to check
     * @return true if the field's type is a primitive type, false otherwise
     */
    public static boolean isPrimitiveType(Field field) {
        return field.getType().isPrimitive();
    }

    /**
     * Creates an instance of a regular (non-record) class and populates its fields with the provided values.
     * Uses the no-argument constructor to create the instance.
     *
     * @param clazz  the class to instantiate
     * @param values a map of fields to their corresponding values
     * @param <T>    the type of the class
     * @return a new instance of the class with fields populated
     * @throws ReflectiveOperationException if an instance cannot be created or fields cannot be set
     */
    private static <T> T createClassInstanceWithValues(Class<T> clazz, Map<Field, Object> values) throws ReflectiveOperationException {
        T instance = clazz.getDeclaredConstructor().newInstance();
        for (Map.Entry<Field, Object> entry : values.entrySet()) {
            Field field = entry.getKey();
            Object value = entry.getValue();
            setFieldValue(instance, field, value);
        }
        return instance;
    }

    /**
     * Creates an instance of a record class and populates its components with the provided values.
     * Uses the canonical constructor. If a value for a component is not provided, its default value is used.
     *
     * @param clazz  the record class to instantiate
     * @param values a map of fields (corresponding to record components) to their values
     * @param <T>    the type of the record class
     * @return a new instance of the record class with components populated
     * @throws ReflectiveOperationException if an instance cannot be created
     */
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

    /**
     * Gets the default value for a given primitive type or null for object types.
     *
     * @param type the class representing the type
     * @return the default value (e.g., 0 for int, false for boolean, null for objects)
     */
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
