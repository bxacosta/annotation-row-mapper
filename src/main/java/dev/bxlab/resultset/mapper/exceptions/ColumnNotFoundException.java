package dev.bxlab.resultset.mapper.exceptions;

import java.io.Serial;

/**
 * Exception thrown when a column specified in the mapping configuration cannot be found
 * in the ResultSet, and unknown columns are not configured to be ignored.
 */
public class ColumnNotFoundException extends MappingException {

    @Serial
    private static final long serialVersionUID = -2832876242479260998L;

    /**
     * The name of the column that was not found.
     */
    private final String columnName;

    /**
     * Constructs a new column not found exception.
     *
     * @param columnName the name of the column that was not found
     */
    public ColumnNotFoundException(String columnName) {
        super("Column not found in ResultSet: " + columnName);
        this.columnName = columnName;
    }

    /**
     * Gets the name of the column that was not found.
     *
     * @return the column name
     */
    @SuppressWarnings("unused")
    public String getColumnName() {
        return columnName;
    }
}
