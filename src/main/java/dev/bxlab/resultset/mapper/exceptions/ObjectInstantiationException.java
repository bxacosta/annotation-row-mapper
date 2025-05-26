package dev.bxlab.resultset.mapper.exceptions;

import java.io.Serial;

/**
 * Exception thrown when an object cannot be instantiated or populated during the mapping process.
 */
public class ObjectInstantiationException extends MappingException {

    @Serial
    private static final long serialVersionUID = 362227393182350949L;
    private final Class<?> targetType;

    /**
     * Constructs a new object instantiation exception.
     *
     * @param targetType the class that could not be instantiated
     * @param cause      the underlying cause of the instantiation failure
     */
    public ObjectInstantiationException(Class<?> targetType, Throwable cause) {
        super("Failed to instantiate object of type: " + targetType.getSimpleName(), cause);
        this.targetType = targetType;
    }

    /**
     * Gets the target type that could not be instantiated.
     *
     * @return the target class
     */
    @SuppressWarnings("unused")
    public Class<?> getTargetType() {
        return targetType;
    }
}
