package  dev.bxlab.resultset_mapper.converters;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;

/**
 * Functional interface for converting database column values from a ResultSet to Java objects.
 * Implementations of this interface define specific conversion strategies for different data types.
 */
@FunctionalInterface
public interface TypeConverter<T> {
    /**
     * Converts a value from the ResultSet to the target type.
     *
     * @param resultSet  the database result set containing the data
     * @param columnName the name of the column to convert
     * @param attributes additional attributes that may influence the conversion process
     * @return the converted value of type T
     * @throws SQLException if a database access error occurs or the column doesn't exist
     */
    T convert(ResultSet resultSet, String columnName, Map<String, Object> attributes) throws SQLException;
}
