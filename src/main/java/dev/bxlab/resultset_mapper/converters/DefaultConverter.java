package  dev.bxlab.resultset_mapper.converters;

import java.sql.ResultSet;
import java.util.Map;

public class DefaultConverter implements TypeConverter<Void> {
    @Override
    public Void convert(ResultSet resultSet, String columnName, Map<String, Object> attributes) {
        throw new UnsupportedOperationException("Default converter does not support conversion");
    }
}