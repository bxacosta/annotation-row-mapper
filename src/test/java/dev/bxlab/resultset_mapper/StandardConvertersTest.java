package dev.bxlab.resultset_mapper;

import dev.bxlab.resultset_mapper.configs.FieldConfig;
import dev.bxlab.resultset_mapper.converters.ConverterRegistry;
import dev.bxlab.resultset_mapper.converters.StandardConverters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class StandardConvertersTest {

    @Mock
    private ResultSet resultSet;

    @Mock
    private ConverterRegistry registry;

    private Map<String, Object> attributes;

    @BeforeEach
    void setUp() {
        attributes = new HashMap<>();
    }

    @Test
    void shouldConvertObjectValue() throws SQLException {
        String columnName = "data";
        Object expectedValue = new Object();
        when(resultSet.getObject(columnName)).thenReturn(expectedValue);

        Object result = StandardConverters.OBJECT.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getObject(columnName);
    }

    @Test
    void shouldConvertStringValue() throws SQLException {
        String columnName = "name";
        String expectedValue = "test value";
        when(resultSet.getString(columnName)).thenReturn(expectedValue);

        String result = StandardConverters.STRING.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getString(columnName);
    }

    @Test
    void shouldReturnNullWhenStringValueIsNull() throws SQLException {
        String columnName = "name";
        when(resultSet.getString(columnName)).thenReturn(null);

        String result = StandardConverters.STRING.convert(resultSet, columnName, attributes);

        assertNull(result);
        verify(resultSet).getString(columnName);
    }

    @Test
    void shouldConvertBigDecimalValue() throws SQLException {
        String columnName = "amount";
        BigDecimal expectedValue = new BigDecimal("123.45");
        when(resultSet.getBigDecimal(columnName)).thenReturn(expectedValue);

        BigDecimal result = StandardConverters.BIG_DECIMAL.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getBigDecimal(columnName);
    }

    @Test
    void shouldReturnNullWhenBigDecimalValueIsNull() throws SQLException {
        String columnName = "amount";
        when(resultSet.getBigDecimal(columnName)).thenReturn(null);

        BigDecimal result = StandardConverters.BIG_DECIMAL.convert(resultSet, columnName, attributes);

        assertNull(result);
        verify(resultSet).getBigDecimal(columnName);
    }

    @Test
    void shouldConvertIntegerValue() throws SQLException {
        String columnName = "id";
        int expectedValue = 42;
        when(resultSet.getInt(columnName)).thenReturn(expectedValue);
        when(resultSet.wasNull()).thenReturn(false);

        Integer result = StandardConverters.INTEGER.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getInt(columnName);
        verify(resultSet).wasNull();
    }

    @Test
    void shouldReturnNullWhenIntegerValueIsNull() throws SQLException {
        String columnName = "id";
        when(resultSet.getInt(columnName)).thenReturn(0); // Default value for int
        when(resultSet.wasNull()).thenReturn(true);

        Integer result = StandardConverters.INTEGER.convert(resultSet, columnName, attributes);

        assertNull(result);
        verify(resultSet).getInt(columnName);
        verify(resultSet).wasNull();
    }

    @Test
    void shouldConvertBooleanValue() throws SQLException {
        String columnName = "active";
        boolean expectedValue = true;
        when(resultSet.getBoolean(columnName)).thenReturn(expectedValue);
        when(resultSet.wasNull()).thenReturn(false);

        Boolean result = StandardConverters.BOOLEAN.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getBoolean(columnName);
        verify(resultSet).wasNull();
    }

    @Test
    void shouldReturnNullWhenBooleanValueIsNull() throws SQLException {
        String columnName = "active";
        when(resultSet.getBoolean(columnName)).thenReturn(false); // Default value for boolean
        when(resultSet.wasNull()).thenReturn(true);

        Boolean result = StandardConverters.BOOLEAN.convert(resultSet, columnName, attributes);

        assertNull(result);
        verify(resultSet).getBoolean(columnName);
        verify(resultSet).wasNull();
    }

    @Test
    void shouldConvertDoubleValue() throws SQLException {
        String columnName = "price";
        double expectedValue = 99.99;
        when(resultSet.getDouble(columnName)).thenReturn(expectedValue);
        when(resultSet.wasNull()).thenReturn(false);

        Double result = StandardConverters.DOUBLE.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getDouble(columnName);
        verify(resultSet).wasNull();
    }

    @Test
    void shouldReturnNullWhenDoubleValueIsNull() throws SQLException {
        String columnName = "price";
        when(resultSet.getDouble(columnName)).thenReturn(0.0); // Default value for double
        when(resultSet.wasNull()).thenReturn(true);

        Double result = StandardConverters.DOUBLE.convert(resultSet, columnName, attributes);

        assertNull(result);
        verify(resultSet).getDouble(columnName);
        verify(resultSet).wasNull();
    }

    @Test
    void shouldConvertFloatValue() throws SQLException {
        String columnName = "rating";
        float expectedValue = 4.5f;
        when(resultSet.getFloat(columnName)).thenReturn(expectedValue);
        when(resultSet.wasNull()).thenReturn(false);

        Float result = StandardConverters.FLOAT.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getFloat(columnName);
        verify(resultSet).wasNull();
    }

    @Test
    void shouldConvertLongValue() throws SQLException {
        String columnName = "timestamp";
        long expectedValue = 1621234567890L;
        when(resultSet.getLong(columnName)).thenReturn(expectedValue);
        when(resultSet.wasNull()).thenReturn(false);

        Long result = StandardConverters.LONG.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getLong(columnName);
        verify(resultSet).wasNull();
    }

    @Test
    void shouldConvertShortValue() throws SQLException {
        String columnName = "code";
        short expectedValue = 32767;
        when(resultSet.getShort(columnName)).thenReturn(expectedValue);
        when(resultSet.wasNull()).thenReturn(false);

        Short result = StandardConverters.SHORT.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getShort(columnName);
        verify(resultSet).wasNull();
    }

    @Test
    void shouldConvertDateValue() throws SQLException {
        String columnName = "registration_date";
        Timestamp timestamp = Timestamp.valueOf(LocalDateTime.parse("2025-05-22T10:15:30"));
        Date expectedValue = new Date(timestamp.getTime());

        when(resultSet.getTimestamp(columnName)).thenReturn(timestamp);

        Date result = StandardConverters.DATE.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getTimestamp(columnName);
    }

    @Test
    void shouldConvertDateValueWithFormat() throws SQLException, ParseException {
        String columnName = "registration_date";
        String dateString = "15/05/2025 14:30:00";
        String format = "dd/MM/yyyy HH:mm:ss";

        attributes.put(FieldConfig.FORMAT_ATTRIBUTE, format);
        when(resultSet.getString(columnName)).thenReturn(dateString);

        SimpleDateFormat dateFormat = new SimpleDateFormat(format);
        Date expectedValue = dateFormat.parse(dateString);

        Date result = StandardConverters.DATE.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getString(columnName);
    }

    @Test
    void shouldConvertLocalDateValue() throws SQLException {
        String columnName = "birth_date";
        LocalDate expectedValue = LocalDate.of(2025, 5, 15);
        java.sql.Date sqlDate = java.sql.Date.valueOf(expectedValue);
        when(resultSet.getDate(columnName)).thenReturn(sqlDate);

        LocalDate result = StandardConverters.LOCAL_DATE.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getDate(columnName);
    }

    @Test
    void shouldConvertLocalDateValueWithFormat() throws SQLException {
        String columnName = "birth_date";
        String dateString = "15/05/2025";
        String format = "dd/MM/yyyy";
        LocalDate expectedValue = LocalDate.of(2025, 5, 15);

        attributes.put(FieldConfig.FORMAT_ATTRIBUTE, format);
        when(resultSet.getString(columnName)).thenReturn(dateString);

        LocalDate result = StandardConverters.LOCAL_DATE.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getString(columnName);
    }

    @Test
    void shouldConvertLocalDateTimeValue() throws SQLException {
        String columnName = "created_at";
        LocalDateTime expectedValue = LocalDateTime.of(2025, 5, 15, 14, 30, 0);
        Timestamp timestamp = Timestamp.valueOf(expectedValue);
        when(resultSet.getTimestamp(columnName)).thenReturn(timestamp);

        LocalDateTime result = StandardConverters.LOCAL_DATE_TIME.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getTimestamp(columnName);
    }

    @Test
    void shouldConvertLocalDateTimeValueWithFormat() throws SQLException {
        String columnName = "created_at";
        String dateTimeString = "15/05/2025 14:30:00";
        String format = "dd/MM/yyyy HH:mm:ss";
        LocalDateTime expectedValue = LocalDateTime.of(2025, 5, 15, 14, 30, 0);

        attributes.put(FieldConfig.FORMAT_ATTRIBUTE, format);
        when(resultSet.getString(columnName)).thenReturn(dateTimeString);

        LocalDateTime result = StandardConverters.LOCAL_DATE_TIME.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getString(columnName);
    }

    @Test
    void shouldConvertZonedDateTimeValue() throws SQLException {
        String columnName = "updated_at";
        ZonedDateTime expectedZonedDateTime = ZonedDateTime.parse("2025-05-22T10:15:30+01:00[Europe/Paris]").withZoneSameInstant(ZoneOffset.UTC);
        when(resultSet.getTimestamp(columnName)).thenReturn(Timestamp.from(expectedZonedDateTime.toInstant()));

        ZonedDateTime result = StandardConverters.ZONED_DATE_TIME.convert(resultSet, columnName, attributes);

        assertEquals(expectedZonedDateTime, result);
        verify(resultSet).getTimestamp(columnName);
    }

    @Test
    void shouldConvertZonedDateTimeValueWithFormat() throws SQLException {
        String columnName = "updated_at";
        String dateTimeString = "2025-05-22 10:15:30 +0100";
        String format = "yyyy-MM-dd HH:mm:ss X";
        ZonedDateTime expectedValue = ZonedDateTime.parse("2025-05-22T10:15:30+01:00").withZoneSameInstant(ZoneOffset.UTC);

        attributes.put(FieldConfig.FORMAT_ATTRIBUTE, format);
        when(resultSet.getString(columnName)).thenReturn(dateTimeString);

        ZonedDateTime result = StandardConverters.ZONED_DATE_TIME.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getString(columnName);
    }

    @Test
    void shouldConvertOffsetDateTimeValue() throws SQLException {
        String columnName = "timestamp";
        OffsetDateTime expectedValue = OffsetDateTime.parse("2025-05-22T10:15:30+01:00").withOffsetSameInstant(ZoneOffset.UTC);
        when(resultSet.getTimestamp(columnName)).thenReturn(Timestamp.from(expectedValue.toInstant()));

        OffsetDateTime result = StandardConverters.OFFSET_DATE_TIME.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getTimestamp(columnName);
    }

    @Test
    void shouldConvertOffsetDateTimeValueWithFormat() throws SQLException {
        String columnName = "timestamp";
        String dateTimeString = "2025-05-22 10:15:30 +0100";
        String format = "yyyy-MM-dd HH:mm:ss X";
        OffsetDateTime expectedValue = OffsetDateTime.parse("2025-05-22T10:15:30+01:00").withOffsetSameInstant(ZoneOffset.UTC);

        attributes.put(FieldConfig.FORMAT_ATTRIBUTE, format);
        when(resultSet.getString(columnName)).thenReturn(dateTimeString);

        OffsetDateTime result = StandardConverters.OFFSET_DATE_TIME.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getString(columnName);
    }

    @Test
    void shouldRegisterAllDefaultConverters() {
        StandardConverters.registerDefaults(registry);

        verify(registry).register(String.class, StandardConverters.STRING);
        verify(registry).register(int.class, StandardConverters.INTEGER);
        verify(registry).register(Integer.class, StandardConverters.INTEGER);
        verify(registry).register(long.class, StandardConverters.LONG);
        verify(registry).register(Long.class, StandardConverters.LONG);
        verify(registry).register(boolean.class, StandardConverters.BOOLEAN);
        verify(registry).register(Boolean.class, StandardConverters.BOOLEAN);
        verify(registry).register(double.class, StandardConverters.DOUBLE);
        verify(registry).register(Double.class, StandardConverters.DOUBLE);
        verify(registry).register(float.class, StandardConverters.FLOAT);
        verify(registry).register(Float.class, StandardConverters.FLOAT);
        verify(registry).register(short.class, StandardConverters.SHORT);
        verify(registry).register(Short.class, StandardConverters.SHORT);
        verify(registry).register(Date.class, StandardConverters.DATE);
        verify(registry).register(LocalDate.class, StandardConverters.LOCAL_DATE);
        verify(registry).register(LocalDateTime.class, StandardConverters.LOCAL_DATE_TIME);
        verify(registry).register(ZonedDateTime.class, StandardConverters.ZONED_DATE_TIME);
        verify(registry).register(OffsetDateTime.class, StandardConverters.OFFSET_DATE_TIME);
        verify(registry).register(BigDecimal.class, StandardConverters.BIG_DECIMAL);
    }
}