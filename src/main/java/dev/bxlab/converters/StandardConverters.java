package dev.bxlab.converters;

import dev.bxlab.configs.FieldConfig;
import dev.bxlab.utils.ConverterUtils;

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

public final class StandardConverters {

    public static final TypeConverter<Object> OBJECT = createBasicConverter(ResultSet::getObject);
    public static final TypeConverter<String> STRING = createBasicConverter(ResultSet::getString);
    public static final TypeConverter<BigDecimal> BIG_DECIMAL = createBasicConverter(ResultSet::getBigDecimal);
    public static final TypeConverter<Integer> INTEGER = createPrimitiveConverter(ResultSet::getInt);
    public static final TypeConverter<Boolean> BOOLEAN = createPrimitiveConverter(ResultSet::getBoolean);
    public static final TypeConverter<Double> DOUBLE = createPrimitiveConverter(ResultSet::getDouble);
    public static final TypeConverter<Float> FLOAT = createPrimitiveConverter(ResultSet::getFloat);
    public static final TypeConverter<Long> LONG = createPrimitiveConverter(ResultSet::getLong);
    public static final TypeConverter<Short> SHORT = createPrimitiveConverter(ResultSet::getShort);
    public static final TypeConverter<Date> DATE = createDateConverter(
            ResultSet::getTimestamp,
            Function.identity(),
            ConverterUtils::toDate
    );
    public static final TypeConverter<LocalDate> LOCAL_DATE = createDateConverter(
            ResultSet::getDate,
            java.sql.Date::toLocalDate,
            ConverterUtils::toLocalDate
    );
    public static final TypeConverter<LocalDateTime> LOCAL_DATE_TIME = createDateConverter(
            ResultSet::getTimestamp,
            Timestamp::toLocalDateTime,
            ConverterUtils::toLocalDateTime
    );
    public static final TypeConverter<ZonedDateTime> ZONED_DATE_TIME = createDateConverter(
            ResultSet::getTimestamp,
            timestamp -> timestamp.toInstant().atZone(ZoneId.systemDefault()),
            ConverterUtils::toZonedDateTime
    );
    private static final TypeConverter<OffsetDateTime> OFFSET_DATE_TIME = createDateConverter(
            ResultSet::getTimestamp,
            timestamp -> timestamp.toInstant().atZone(ZoneId.systemDefault()).toOffsetDateTime(),
            ConverterUtils::toOffsetDateTime
    );

    private StandardConverters() {
    }

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
        registry.register(BigDecimal.class, BIG_DECIMAL);
    }

    private static <T> TypeConverter<T> createPrimitiveConverter(ResultSetGetter<T> getter) {
        return (resultSet, columnName, attributes) -> {
            T value = getter.get(resultSet, columnName);
            return resultSet.wasNull() ? null : value;
        };
    }

    private static <T> TypeConverter<T> createBasicConverter(ResultSetGetter<T> getter) {
        return (resultSet, columnName, attributes) -> getter.get(resultSet, columnName);
    }

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

    @FunctionalInterface
    private interface ResultSetGetter<T> {
        T get(ResultSet resultSet, String columnName) throws SQLException;
    }
}