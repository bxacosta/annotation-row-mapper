package dev.bxlab.converters;

import dev.bxlab.configs.FieldConfig;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface TypeConverter<T> {
    T convert(ResultSet resultSet, FieldConfig config) throws SQLException;
}
