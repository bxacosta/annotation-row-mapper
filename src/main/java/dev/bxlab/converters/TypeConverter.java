package dev.bxlab.converters;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

@FunctionalInterface
public interface TypeConverter<T> {
    T convert(ResultSet resultSet, String columnName, Map<String, Object> attributes) throws SQLException;
}
