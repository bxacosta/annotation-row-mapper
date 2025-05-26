package dev.bxlab.resultset.mapper.exceptions;

import java.io.Serial;

/**
 * Exception thrown when a type converter fails to convert a value from the ResultSet
 * to the target field type.
 */
public class ConversionException extends MappingException {

    @Serial
    private static final long serialVersionUID = 4422242815376307042L;

    /**
     * Constructs a new conversion exception with a message and cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause of the conversion failure
     */
    public ConversionException(String message, Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a new conversion exception with just a message.
     *
     * @param message the detail message
     */
    @SuppressWarnings("unused")
    public ConversionException(String message) {
        super(message);
    }
}
