package ru.iisuslik;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;

/**
 * Subsidiary interface for Thread Pool, it relates with one task that is calculating in it.
 * With this you can check if task is ready, get result and create new task that uses current
 * task's result as argument
 */
public interface LightFuture<T> {
    /**
     * The function to check if task is ready(some thread completed it)
     *
     * @return True if this task is ready
     */
    boolean isReady();

    /**
     * Get result of running task
     *
     * @return Supplier or function result
     * @throws LightExecutionException It'll be thrown if Supplier or Function inside throw some Exception.
     *                                 This exception will be added to suppressed exceptions of LightExecutionException.
     */
    T get() throws LightExecutionException;

    /**
     * Creates new LightFuture task that is Function inside, it will take as an argument the result of this task
     * It will be calculated only after this task will be ready
     *
     * @param func Function that will calculate returning LightFuture
     * @return Task with Function inside
     */
    LightFuture<T> thenApply(@NotNull Function<T, T> func);
}
