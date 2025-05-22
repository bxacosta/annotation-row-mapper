package  dev.bxlab.resultset_mapper.utils;

import  dev.bxlab.resultset_mapper.converters.DefaultConverter;
import  dev.bxlab.resultset_mapper.converters.TypeConverter;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

/**
 * Utility class for type conversions, primarily for date and time types.
 * Also provides a method to check if a {@link TypeConverter} is a {@link DefaultConverter}.
 */
public final class ConverterUtils {
    private ConverterUtils() {
    }

    /**
     * Converts a string value to a {@link LocalDate} using the specified format.
     *
     * @param value  the string value to convert
     * @param format the date format pattern
     * @return the parsed {@link LocalDate}
     */
    public static LocalDate toLocalDate(String value, String format) {
        return LocalDate.parse(value, DateTimeFormatter.ofPattern(format));
    }

    /**
     * Converts a string value to a {@link LocalDateTime} using the specified format.
     *
     * @param value  the string value to convert
     * @param format the date-time format pattern
     * @return the parsed {@link LocalDateTime}
     */
    public static LocalDateTime toLocalDateTime(String value, String format) {
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format));
    }

    /**
     * Converts a string value to a {@link ZonedDateTime} using the specified format.
     *
     * @param value  the string value to convert
     * @param format the date-time format pattern
     * @return the parsed {@link ZonedDateTime}
     */
    public static ZonedDateTime toZonedDateTime(String value, String format) {
        return ZonedDateTime.parse(value, DateTimeFormatter.ofPattern(format));
    }

    /**
     * Converts a string value to an {@link OffsetDateTime} using the specified format.
     *
     * @param value  the string value to convert
     * @param format the date-time format pattern
     * @return the parsed {@link OffsetDateTime}
     */
    public static OffsetDateTime toOffsetDateTime(String value, String format) {
        return OffsetDateTime.parse(value, DateTimeFormatter.ofPattern(format));
    }

    /**
     * Converts a string value to a {@link Date} using the specified format.
     *
     * @param value  the string value to convert
     * @param format the date format pattern
     * @return the parsed {@link Date}
     * @throws IllegalStateException if the date string cannot be parsed with the given format
     */
    public static Date toDate(String value, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return ExceptionHandler.map(() -> sdf.parse(value),
                e -> new IllegalStateException("Cannot parse date '" + value + "' with format '" + format + "'", e));
    }

    /**
     * Checks if the given {@link TypeConverter} is an instance of {@link DefaultConverter}.
     *
     * @param converter the type converter to check
     * @return true if the converter is a {@link DefaultConverter}, false otherwise
     */
    public static boolean isDefaultConverter(TypeConverter<?> converter) {
        return converter instanceof DefaultConverter;
    }
}
