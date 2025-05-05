package dev.bxlab.core;

import dev.bxlab.converters.DefaultConverter;
import dev.bxlab.converters.TypeConverter;

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