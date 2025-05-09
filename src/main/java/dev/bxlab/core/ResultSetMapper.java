package dev.bxlab.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@FunctionalInterface
public interface ResultSetMapper<T> {
    T map(ResultSet resultSet) throws SQLException;

    default List<T> mapAll(ResultSet resultSet) throws SQLException {
        List<T> results = new ArrayList<>();
        while (resultSet.next()) results.add(map(resultSet));
        return results;
    }
}
