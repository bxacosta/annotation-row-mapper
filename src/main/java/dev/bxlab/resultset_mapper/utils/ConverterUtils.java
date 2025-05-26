package dev.bxlab.resultset_mapper.utils;

import dev.bxlab.resultset_mapper.converters.DefaultConverter;
import dev.bxlab.resultset_mapper.converters.TypeConverter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
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
     * @throws DateTimeParseException if the text cannot be parsed
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
     * @throws DateTimeParseException if the text cannot be parsed
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
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static ZonedDateTime toZonedDateTime(String value, String format) {
        return ZonedDateTime.parse(value, DateTimeFormatter.ofPattern(format)).withZoneSameInstant(ZoneOffset.UTC);
    }

    /**
     * Converts a string value to an {@link OffsetDateTime} using the specified format.
     *
     * @param value  the string value to convert
     * @param format the date-time format pattern
     * @return the parsed {@link OffsetDateTime}
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static OffsetDateTime toOffsetDateTime(String value, String format) {
        return OffsetDateTime.parse(value, DateTimeFormatter.ofPattern(format)).withOffsetSameInstant(ZoneOffset.UTC);
    }

    /**
     * Converts a string value to a {@link Date} using the specified format.
     *
     * @param value  the string value to convert
     * @param format the date format pattern
     * @return the parsed {@link Date}
     * @throws DateTimeParseException if the text cannot be parsed
     */
    public static Date toDate(String value, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        try {
            return sdf.parse(value);
        } catch (ParseException e) {
            throw new DateTimeParseException(e.getMessage(), value, e.getErrorOffset(), e);
        }
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
