package dev.bxlab.resultset.mapper.converters;

import java.sql.ResultSet;
import java.util.Map;

/**
 * A default TypeConverter that does nothing.
 * <p>
 * Throws {@link UnsupportedOperationException} if {@code convert} is called.
 * Indicates that a globally registered or standard converter should be used.
 */
public final class DefaultConverter implements TypeConverter<Void> {

    /**
     * Default constructor.
     */
    public DefaultConverter() {
    }

    @Override
    public Void convert(ResultSet resultSet, String columnName, Map<String, Object> attributes) {
        throw new UnsupportedOperationException("Default converter does not support conversion");
    }
}