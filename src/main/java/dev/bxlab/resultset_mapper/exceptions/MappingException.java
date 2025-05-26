package dev.bxlab.resultset_mapper.exceptions;

import java.io.Serial;

/**
 * Base exception for all mapping-related errors in the ResultSet mapper.
 * This exception serves as the parent for all specific mapping exceptions.
 */
public class MappingException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 6403477792357933497L;

    /**
     * Constructs a new mapping exception with the specified detail message.
     *
     * @param message the detail message
     */
    public MappingException(String message) {
        super(message);
    }

    /**
     * Constructs a new mapping exception with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause   the cause of this exception
     */
    public MappingException(String message, Throwable cause) {
        super(message, cause);
    }
}
