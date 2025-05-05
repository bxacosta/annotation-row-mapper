package dev.bxlab.utils;

import java.util.function.Function;

public final class ExceptionHandler {

    private ExceptionHandler() {
    }

    public static <T, E extends Exception> T map(ThrowingSupplier<T> action, Function<? super Exception, ? extends E> mapper) throws E {
        try {
            return action.get();
        } catch (Exception e) {
            throw mapper.apply(e);
        }
    }

    public static <E extends Exception> void map(ThrowingRunner action, Function<? super Exception, ? extends E> mapper) throws E {
        try {
            action.run();
        } catch (Exception e) {
            throw mapper.apply(e);
        }
    }

    @FunctionalInterface
    public interface ThrowingSupplier<T> {
        T get() throws Exception;
    }

    @FunctionalInterface
    public interface ThrowingRunner {
        void run() throws Exception;
    }
}
