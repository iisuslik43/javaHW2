package ru.iisuslik.lazy;

/**
 * Class that works like Supplier, but function computation happens only 1 time, instead of
 * computation every time when you called get() in simple Supplier
 *
 * @param <T>
 */
public interface Lazy<T> {
    /**
     * Function to get result of calculation, that calls only 1 time
     *
     * @return Calculation result
     */
    T get();
}
