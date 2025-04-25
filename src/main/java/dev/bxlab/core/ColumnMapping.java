package dev.bxlab.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ColumnMapping {
    String value();

    String format() default "";

    @SuppressWarnings("java:S1452")
    Class<? extends ValueConverter<?>> converter() default DefaultValueConverter.class;
}