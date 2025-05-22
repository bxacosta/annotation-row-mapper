package dev.bxlab.resultset_mapper.converters;

import dev.bxlab.resultset_mapper.configs.FieldConfig;
import dev.bxlab.resultset_mapper.utils.ConverterUtils;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

/**
 * Provides standard type converters for mapping ResultSet column values to Java objects.
 * This class contains predefined converters for common Java types and utility methods
 * for creating custom converters.
 */
public final class StandardConverters {

    /** Standard converter for Object values */
    public static final TypeConverter<Object> OBJECT = createBasicConverter(ResultSet::getObject);
    /** Standard converter for String values */
    public static final TypeConverter<String> STRING = createBasicConverter(ResultSet::getString);
    /** Standard converter for BigDecimal values */
    public static final TypeConverter<BigDecimal> BIG_DECIMAL = createBasicConverter(ResultSet::getBigDecimal);
    /** Standard converter for Integer values with null handling */
    public static final TypeConverter<Integer> INTEGER = createPrimitiveConverter(ResultSet::getInt);
    /** Standard converter for Boolean values with null handling */
    public static final TypeConverter<Boolean> BOOLEAN = createPrimitiveConverter(ResultSet::getBoolean);
    /** Standard converter for Double values with null handling */
    public static final TypeConverter<Double> DOUBLE = createPrimitiveConverter(ResultSet::getDouble);
    /** Standard converter for Float values with null handling */
    public static final TypeConverter<Float> FLOAT = createPrimitiveConverter(ResultSet::getFloat);
    /** Standard converter for Long values with null handling */
    public static final TypeConverter<Long> LONG = createPrimitiveConverter(ResultSet::getLong);
    /** Standard converter for Short values with null handling */
    public static final TypeConverter<Short> SHORT = createPrimitiveConverter(ResultSet::getShort);
    /** Standard converter for Date values with format support */
    public static final TypeConverter<Date> DATE = createDateConverter(
            ResultSet::getTimestamp,
            Function.identity(),
            ConverterUtils::toDate
    );
    /** Standard converter for LocalDate values with format support */
    public static final TypeConverter<LocalDate> LOCAL_DATE = createDateConverter(
            ResultSet::getDate,
            java.sql.Date::toLocalDate,
            ConverterUtils::toLocalDate
    );
    /** Standard converter for LocalDateTime values with format support */
    public static final TypeConverter<LocalDateTime> LOCAL_DATE_TIME = createDateConverter(
            ResultSet::getTimestamp,
            Timestamp::toLocalDateTime,
            ConverterUtils::toLocalDateTime
    );
    /** Standard converter for ZonedDateTime values with format support */
    public static final TypeConverter<ZonedDateTime> ZONED_DATE_TIME = createDateConverter(
            ResultSet::getTimestamp,
            timestamp -> timestamp.toInstant().atZone(ZoneId.systemDefault()),
            ConverterUtils::toZonedDateTime
    );
    /** Standard converter for OffsetDateTime values with format support */
    private static final TypeConverter<OffsetDateTime> OFFSET_DATE_TIME = createDateConverter(
            ResultSet::getTimestamp,
            timestamp -> timestamp.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime(),
            ConverterUtils::toOffsetDateTime
    );


    private StandardConverters() {
    }

    /**
     * Registers all standard type converters with the provided registry.
     *
     * @param registry the converter registry to populate with standard converters
     */
    public static void registerDefaults(ConverterRegistry registry) {
        registry.register(String.class, STRING);
        registry.register(int.class, INTEGER);
        registry.register(Integer.class, INTEGER);
        registry.register(long.class, LONG);
        registry.register(Long.class, LONG);
        registry.register(boolean.class, BOOLEAN);
        registry.register(Boolean.class, BOOLEAN);
        registry.register(double.class, DOUBLE);
        registry.register(Double.class, DOUBLE);
        registry.register(float.class, FLOAT);
        registry.register(Float.class, FLOAT);
        registry.register(short.class, SHORT);
        registry.register(Short.class, SHORT);

        registry.register(Date.class, DATE);
        registry.register(LocalDate.class, LOCAL_DATE);
        registry.register(LocalDateTime.class, LOCAL_DATE_TIME);
        registry.register(ZonedDateTime.class, ZONED_DATE_TIME);
        registry.register(OffsetDateTime.class, OFFSET_DATE_TIME);
        registry.register(BigDecimal.class, BIG_DECIMAL);
    }

    /**
     * Creates a converter for primitive types that handles null values correctly.
     *
     * @param <T> the target type of the converter
     * @param getter the function to extract values from the ResultSet
     * @return a TypeConverter that converts ResultSet values to the target type
     */
    private static <T> TypeConverter<T> createPrimitiveConverter(ResultSetGetter<T> getter) {
        return (resultSet, columnName, attributes) -> {
            T value = getter.get(resultSet, columnName);
            return resultSet.wasNull() ? null : value;
        };
    }

    /**
     * Creates a basic converter for reference types.
     *
     * @param <T> the target type of the converter
     * @param getter the function to extract values from the ResultSet
     * @return a TypeConverter that converts ResultSet values to the target type
     */
    private static <T> TypeConverter<T> createBasicConverter(ResultSetGetter<T> getter) {
        return (resultSet, columnName, attributes) -> getter.get(resultSet, columnName);
    }

    /**
     * Creates a converter for date/time types with optional format support.
     *
     * @param <T> the target date/time type
     * @param <U> the intermediate type from the ResultSet
     * @param getter the function to extract values from the ResultSet
     * @param converter the function to convert from intermediate to target type
     * @param formatter the function to parse string values using a format pattern
     * @return a TypeConverter that converts ResultSet values to the target date/time type
     */
    private static <T, U> TypeConverter<T> createDateConverter(
            ResultSetGetter<U> getter,
            Function<U, T> converter,
            BiFunction<String, String, T> formatter) {

        return (resultSet, columnName, attributes) -> {
            Optional<String> format = Optional.ofNullable((String) attributes.get(FieldConfig.FORMAT_ATTRIBUTE));

            return format.isEmpty()
                    ? Optional.ofNullable(getter.get(resultSet, columnName)).map(converter).orElse(null)
                    : Optional.ofNullable(resultSet.getString(columnName))
                    .map(value -> formatter.apply(value, format.get())).orElse(null);
        };
    }

    /**
     * Functional interface for extracting typed values from a ResultSet by column name.
     *
     * @param <T> the type of value to extract
     */
    @FunctionalInterface
    private interface ResultSetGetter<T> {
        T get(ResultSet resultSet, String columnName) throws SQLException;
    }
}