package dev.bxlab.core;

import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

public class DefaultValueConverter implements ValueConverter<Object> {
    @Override
    public Object convert(ResultSet resultSet, String columnName, MappingInfo mappingInfo) throws SQLException {
        if (resultSet.getObject(columnName) == null) return null;

        Class<?> targetType = mappingInfo.field().getType();

        switch (targetType.getName()) {
            case "java.lang.String":
                return resultSet.getString(columnName);
            case "java.lang.Long":
            case "long": {
                long value = resultSet.getLong(columnName);
                return resultSet.wasNull() ? null : value;
            }
            case "java.lang.Integer":
            case "int": {
                int value = resultSet.getInt(columnName);
                return resultSet.wasNull() ? null : value;
            }
            case "java.math.BigDecimal":
                return resultSet.getBigDecimal(columnName);
            case "java.lang.Boolean":
            case "boolean": {
                boolean value = resultSet.getBoolean(columnName);
                return resultSet.wasNull() ? null : value;
            }
            case "java.time.LocalDate": {
                String format = mappingInfo.format();

                if (!format.isEmpty()) {
                    return Optional.ofNullable(resultSet.getString(columnName))
                            .map(stringValue -> LocalDate.parse(stringValue, DateTimeFormatter.ofPattern(format)))
                            .orElse(null);
                }

                return Optional.ofNullable(resultSet.getDate(columnName)).map(Date::toLocalDate).orElse(null);
            }
            case "java.time.LocalDateTime": {
                String format = mappingInfo.format();

                if (!format.isEmpty()) {
                    return Optional.ofNullable(resultSet.getString(columnName))
                            .map(stringValue -> LocalDateTime.parse(stringValue, DateTimeFormatter.ofPattern(format)))
                            .orElse(null);
                }

                return Optional.ofNullable(resultSet.getTimestamp(columnName))
                        .map(Timestamp::toLocalDateTime)
                        .orElse(null);
            }
            case "java.lang.Double":
            case "double": {
                double value = resultSet.getDouble(columnName);
                return resultSet.wasNull() ? null : value;
            }
            default:
                return resultSet.getObject(columnName);
        }
    }
}
