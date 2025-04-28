package dev.bxlab.converters;

import dev.bxlab.core.MappingInfo;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface TypeConverter<T> {
    T convert(ResultSet resultSet, MappingInfo mappingInfo) throws SQLException;
}
