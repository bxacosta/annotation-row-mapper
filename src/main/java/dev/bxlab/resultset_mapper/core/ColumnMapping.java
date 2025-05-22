package dev.bxlab.resultset_mapper.core;

import dev.bxlab.resultset_mapper.converters.DefaultConverter;
import dev.bxlab.resultset_mapper.converters.TypeConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark a field in a class to be mapped from a ResultSet column.
 * It allows specifying the column name, a format string (e.g., for dates),
 * and a custom {@link TypeConverter}.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnMapping {

    /**
     * Specifies the name of the database column to map to this field.
     * If not specified, the field name itself (possibly transformed by a {@link dev.bxlab.resultset_mapper.configs.NamingStrategy}) will be used.
     *
     * @return the name of the database column
     */
    String value() default "";

    /**
     * Specifies a format string, typically used for date/time parsing or other type-specific formatting.
     * The interpretation of this format depends on the {@link TypeConverter} being used.
     *
     * @return the format string
     */
    String format() default "";

    /**
     * Specifies a custom {@link TypeConverter} class to use for converting the ResultSet value to the field's type.
     * Defaults to {@link DefaultConverter}, which indicates that a globally registered or standard converter should be used.
     *
     * @return the class of the custom type converter
     */
    Class<? extends TypeConverter<?>> converter() default DefaultConverter.class;
}