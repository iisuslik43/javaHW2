package ru.iisuslik.lazy;

import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

/**
 * Class from where you can get 2 realization of Lazy interface - simple and multithreaded safe
 */
public class LazyFactory {


    /**
     * Returns class that implements Lazy interface
     *
     * @param function Supplier that will be called
     * @param <T>      Typ of supplier return value
     * @return New Lazy from supplier
     */
    public static <T> Lazy<T> createLazy(@NotNull Supplier<T> function) {
        return new Lazy<T>() {
            private Supplier<T> func = function;
            private T result;

            @Override
            public T get() {
                if (func == null)
                    return result;
                result = func.get();
                func = null;
                return result;
            }
        };
    }

    /**
     * Returns class that implements Lazy interface, but work correctly with many threads
     * It uses "synchronized" so it can work slower
     *
     * @param function Supplier that will be called
     * @param <T>      Typ of supplier return value
     * @return New Lazy from supplier
     */
    public static <T> Lazy<T> createLazySafe(@NotNull Supplier<T> function) {
        return new Lazy<T>() {
            private Supplier<T> func = function;
            private T result;

            @Override
            public T get() {
                if (func == null)
                    return result;
                synchronized (this) {
                    if (func != null) {
                        result = func.get();
                        func = null;
                    }
                }
                return result;
            }
        };
    }
}
