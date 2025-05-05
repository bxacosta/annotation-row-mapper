package dev.bxlab.converters;

import dev.bxlab.configs.FieldConfig;

import java.sql.ResultSet;

public class DefaultConverter implements TypeConverter<Void> {
    @Override
    public Void convert(ResultSet resultSet, FieldConfig fieldConfig) {
        return null;
    }
}