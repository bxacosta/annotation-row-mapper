package dev.bxlab.core;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ValueConverter<T> {

    T convert(ResultSet resultSet, MappingContext mappingContext) throws SQLException;
}
