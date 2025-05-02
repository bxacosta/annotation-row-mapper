package dev.bxlab.utils;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public final class DateParserUtils {
    private DateParserUtils() {
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

    public static Date toDate(String value, String format) {
        Instant instant = toLocalDateTime(value, format).atZone(ZoneId.systemDefault()).toInstant();
        return Date.from(instant);
    }
}
