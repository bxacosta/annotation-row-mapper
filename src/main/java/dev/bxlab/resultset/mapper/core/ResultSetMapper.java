package dev.bxlab.resultset.mapper.core;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Functional interface for mapping a single row of a {@link ResultSet} to a Java object.
 * Intended for use as a row mapper in database operations.
 *
 * @param <T> the type of the object to map to
 */
@FunctionalInterface
public interface ResultSetMapper<T> {

    /**
     * Maps the current row of the given {@link ResultSet} to an object of type {@code T}.
     *
     * @param resultSet the ResultSet to map from, positioned at the row to be mapped
     * @return an object of type {@code T} populated with data from the current ResultSet row
     * @throws SQLException if a database access error occurs or this method is called on a closed result set
     */
    T map(ResultSet resultSet) throws SQLException;

    /**
     * Maps all rows of the given {@link ResultSet} to a list of objects.
     * @param resultSet the result set to map
     * @return a list of mapped objects
     * @throws SQLException if a database access error occurs
     */
    default List<T> mapAll(ResultSet resultSet) throws SQLException {
        List<T> results = new ArrayList<>();
        while (resultSet.next()) results.add(map(resultSet));
        return results;
    }
}
