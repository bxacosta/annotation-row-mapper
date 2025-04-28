package dev.bxlab.converters;

import dev.bxlab.core.MappingInfo;
import dev.bxlab.core.ValueConverter;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DefaultValueConverter implements ValueConverter<Object> {

    public static TypeConverter<LocalDate> localDateConverter = (ResultSet resultSet, MappingInfo mappingInfo) -> {
        String format = mappingInfo.format();
        String columnName = mappingInfo.columnName();

        if (!format.isEmpty()) {
            return Optional.ofNullable(resultSet.getString(columnName))
                    .map(stringValue -> LocalDate.parse(stringValue, DateTimeFormatter.ofPattern(format)))
                    .orElse(null);
        }

        return Optional.ofNullable(resultSet.getDate(columnName))
                .map(Date::toLocalDate)
                .orElse(null);
    };

    public static TypeConverter<LocalDateTime> localDateTimeConverter = (ResultSet resultSet, MappingInfo mappingInfo) -> {
        String format = mappingInfo.format();
        String columnName = mappingInfo.columnName();

        if (!format.isEmpty()) {
            return Optional.ofNullable(resultSet.getString(columnName))
                    .map(stringValue -> LocalDateTime.parse(stringValue, DateTimeFormatter.ofPattern(format)))
                    .orElse(null);
        }

        return Optional.ofNullable(resultSet.getTimestamp(columnName))
                .map(Timestamp::toLocalDateTime)
                .orElse(null);
    };

    @Override
    public Object convert(ResultSet resultSet, MappingInfo mappingInfo) throws SQLException {
        String columnName = mappingInfo.columnName();

        if (resultSet.getObject(columnName) == null) return null;

        Class<?> targetType = mappingInfo.field().getType();

        return switch (targetType.getName()) {
            case "java.lang.String" -> resultSet.getString(columnName);
            case "java.lang.Long", "long" -> {
                long value = resultSet.getLong(columnName);
                yield resultSet.wasNull() ? null : value;
            }
            case "java.lang.Integer", "int" -> {
                int value = resultSet.getInt(columnName);
                yield resultSet.wasNull() ? null : value;
            }
            case "java.math.BigDecimal" -> resultSet.getBigDecimal(columnName);
            case "java.lang.Boolean", "boolean" -> {
                boolean value = resultSet.getBoolean(columnName);
                yield resultSet.wasNull() ? null : value;
            }
            case "java.time.LocalDate" -> localDateConverter.convert(resultSet, mappingInfo);
            case "java.time.LocalDateTime" -> localDateTimeConverter.convert(resultSet, mappingInfo);
            case "java.lang.Double", "double" -> {
                double value = resultSet.getDouble(columnName);
                yield resultSet.wasNull() ? null : value;
            }
            default -> resultSet.getObject(columnName);
        };
    }
}
