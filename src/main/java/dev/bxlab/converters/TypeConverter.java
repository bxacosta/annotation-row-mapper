package dev.bxlab.converters;

import dev.bxlab.core.MappingContext;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface TypeConverter<T> {
    T convert(ResultSet resultSet, MappingContext mappingContext) throws SQLException;
}
