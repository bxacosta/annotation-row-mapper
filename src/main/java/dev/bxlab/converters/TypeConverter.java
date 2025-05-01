package dev.bxlab.converters;

import dev.bxlab.core.FieldMappingConfig;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface TypeConverter<T> {
    T convert(ResultSet resultSet, FieldMappingConfig config) throws SQLException;
}
