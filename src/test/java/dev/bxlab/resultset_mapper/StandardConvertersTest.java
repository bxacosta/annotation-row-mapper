package  dev.bxlab.resultset_mapper;

import  dev.bxlab.resultset_mapper.configs.FieldConfig;
import  dev.bxlab.resultset_mapper.converters.ConverterRegistry;
import  dev.bxlab.resultset_mapper.converters.StandardConverters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

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
    void testStringConverter() throws SQLException {
        String columnName = "name";
        String expectedValue = "test value";
        when(resultSet.getString(columnName)).thenReturn(expectedValue);

        String result = StandardConverters.STRING.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getString(columnName);
    }

    @Test
    void testStringConverterWithNull() throws SQLException {
        String columnName = "name";
        when(resultSet.getString(columnName)).thenReturn(null);

        String result = StandardConverters.STRING.convert(resultSet, columnName, attributes);

        assertNull(result);
        verify(resultSet).getString(columnName);
    }

    @Test
    void testIntegerConverter() throws SQLException {
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
    void testIntegerConverterWithNull() throws SQLException {
        String columnName = "id";
        when(resultSet.getInt(columnName)).thenReturn(0); // Valor por defecto para int
        when(resultSet.wasNull()).thenReturn(true);

        Integer result = StandardConverters.INTEGER.convert(resultSet, columnName, attributes);

        assertNull(result);
        verify(resultSet).getInt(columnName);
        verify(resultSet).wasNull();
    }

    @Test
    void testBooleanConverter() throws SQLException {
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
    void testBooleanConverterWithNull() throws SQLException {
        String columnName = "active";
        when(resultSet.getBoolean(columnName)).thenReturn(false); // Valor por defecto para boolean
        when(resultSet.wasNull()).thenReturn(true);

        Boolean result = StandardConverters.BOOLEAN.convert(resultSet, columnName, attributes);

        assertNull(result);
        verify(resultSet).getBoolean(columnName);
        verify(resultSet).wasNull();
    }

    @Test
    void testDoubleConverter() throws SQLException {
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
    void testBigDecimalConverter() throws SQLException {
        String columnName = "amount";
        BigDecimal expectedValue = new BigDecimal("123.45");
        when(resultSet.getBigDecimal(columnName)).thenReturn(expectedValue);

        BigDecimal result = StandardConverters.BIG_DECIMAL.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getBigDecimal(columnName);
    }

    @Test
    void testLocalDateConverter() throws SQLException {
        String columnName = "birth_date";
        LocalDate expectedValue = LocalDate.of(2023, 10, 15);
        java.sql.Date sqlDate = java.sql.Date.valueOf(expectedValue);
        when(resultSet.getDate(columnName)).thenReturn(sqlDate);

        LocalDate result = StandardConverters.LOCAL_DATE.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getDate(columnName);
    }

    @Test
    void testLocalDateConverterWithFormat() throws SQLException {
        String columnName = "birth_date";
        String dateString = "15/10/2023";
        String format = "dd/MM/yyyy";
        LocalDate expectedValue = LocalDate.of(2023, 10, 15);

        attributes.put(FieldConfig.FORMAT_ATTRIBUTE, format);
        when(resultSet.getString(columnName)).thenReturn(dateString);

        LocalDate result = StandardConverters.LOCAL_DATE.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getString(columnName);
    }

    @Test
    void testLocalDateTimeConverter() throws SQLException {
        String columnName = "created_at";
        LocalDateTime expectedValue = LocalDateTime.of(2023, 10, 15, 14, 30, 0);
        Timestamp timestamp = Timestamp.valueOf(expectedValue);
        when(resultSet.getTimestamp(columnName)).thenReturn(timestamp);

        LocalDateTime result = StandardConverters.LOCAL_DATE_TIME.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getTimestamp(columnName);
    }

    @Test
    void testZonedDateTimeConverter() throws SQLException {
        String columnName = "updated_at";
        LocalDateTime localDateTime = LocalDateTime.of(2023, 10, 15, 14, 30, 0);
        ZonedDateTime expectedValue = localDateTime.atZone(ZoneId.systemDefault());
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        when(resultSet.getTimestamp(columnName)).thenReturn(timestamp);

        ZonedDateTime result = StandardConverters.ZONED_DATE_TIME.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue.toLocalDateTime(), result.toLocalDateTime());
        verify(resultSet).getTimestamp(columnName);
    }

    @Test
    void testDateConverter() throws SQLException {
        String columnName = "registration_date";
        LocalDateTime localDateTime = LocalDateTime.of(2023, 10, 15, 14, 30, 0);
        Timestamp timestamp = Timestamp.valueOf(localDateTime);
        Date expectedValue = Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
        when(resultSet.getTimestamp(columnName)).thenReturn(timestamp);

        Date result = StandardConverters.DATE.convert(resultSet, columnName, attributes);

        assertEquals(expectedValue, result);
        verify(resultSet).getTimestamp(columnName);
    }

    @Test
    void testRegisterDefaults() {
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
        verify(registry).register(BigDecimal.class, StandardConverters.BIG_DECIMAL);
    }
}