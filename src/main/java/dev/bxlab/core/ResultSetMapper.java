package dev.bxlab.core;

import java.sql.ResultSet;
import java.sql.SQLException;

@FunctionalInterface
public interface ResultSetMapper<T> {
    T mapRow(ResultSet resultSet) throws SQLException;
}
