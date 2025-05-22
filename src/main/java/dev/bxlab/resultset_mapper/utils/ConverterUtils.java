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

public final class ConverterUtils {
    private ConverterUtils() {
    }

    public static LocalDate toLocalDate(String value, String format) {
        return LocalDate.parse(value, DateTimeFormatter.ofPattern(format));
    }

    public static LocalDateTime toLocalDateTime(String value, String format) {
        return LocalDateTime.parse(value, DateTimeFormatter.ofPattern(format));
    }

    public static ZonedDateTime toZonedDateTime(String value, String format) {
        return ZonedDateTime.parse(value, DateTimeFormatter.ofPattern(format));
    }

    public static OffsetDateTime toOffsetDateTime(String value, String format) {
        return OffsetDateTime.parse(value, DateTimeFormatter.ofPattern(format));
    }

    public static Date toDate(String value, String format) {
        SimpleDateFormat sdf = new SimpleDateFormat(format);
        return ExceptionHandler.map(() -> sdf.parse(value),
                e -> new IllegalStateException("Cannot parse date '" + value + "' with format '" + format + "'", e));
    }

    public static boolean isDefaultConverter(TypeConverter<?> converter) {
        return converter instanceof DefaultConverter;
    }
}
