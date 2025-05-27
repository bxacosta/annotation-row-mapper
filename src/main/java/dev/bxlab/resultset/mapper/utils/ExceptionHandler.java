package dev.bxlab.resultset.mapper.utils;

import java.util.function.Function;

/**
 * Utility class for handling exceptions by mapping them to a different type.
 * Provides methods to wrap operations that might throw exceptions and transform those exceptions.
 */
public final class ExceptionHandler {

    private ExceptionHandler() {
    }

    /**
     * Executes an action that returns a value and potentially throws an exception.
     * If an exception occurs, it is mapped to another exception type using the provided mapper function.
     *
     * @param action the action to execute, represented by a {@link ThrowingSupplier}
     * @param mapper a function to map the caught exception to the desired exception type
     * @param <T>    the type of the value returned by the action
     * @param <E>    the type of the exception that can be thrown by this method
     * @return the result of the action if successful
     * @throws E if the action throws an exception, which is then mapped by the mapper function
     */
    public static <T, E extends Exception> T map(ThrowingSupplier<T> action, Function<? super Exception, ? extends E> mapper) throws E {
        try {
            return action.get();
        } catch (Exception e) {
            throw mapper.apply(e);
        }
    }

    /**
     * Executes an action that does not return a value but may throw an exception.
     * If an exception occurs, it is mapped to another exception type using the provided mapper function.
     *
     * @param action the action to execute, represented by a {@link ThrowingRunner}
     * @param mapper a function to map the caught exception to the desired exception type
     * @param <E>    the type of the exception that can be thrown by this method
     * @throws E if the action throws an exception, which is then mapped by the mapper function
     */
    public static <E extends Exception> void map(ThrowingRunner action, Function<? super Exception, ? extends E> mapper) throws E {
        try {
            action.run();
        } catch (Exception e) {
            throw mapper.apply(e);
        }
    }

    /**
     * A functional interface for an operation that supplies a result and may throw an exception.
     *
     * @param <T> the type of the result supplied by this operation
     */
    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        /**
         * Gets the result of the operation.
         * @return the result
         * @throws Exception if the operation fails
         */
        T get() throws Exception;
    }

    /**
     * A functional interface for an operation that performs an action and may throw an exception.
     */
    @FunctionalInterface
    public interface ThrowingRunner {
        /**
         * Runs the operation.
         * @throws Exception if the operation fails
         */
        void run() throws Exception;
    }
}
