package  dev.bxlab.resultset_mapper.core;

import  dev.bxlab.resultset_mapper.converters.DefaultConverter;
import  dev.bxlab.resultset_mapper.converters.TypeConverter;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnMapping {
    String value() default "";

    String format() default "";

    Class<? extends TypeConverter<?>> converter() default DefaultConverter.class;
}