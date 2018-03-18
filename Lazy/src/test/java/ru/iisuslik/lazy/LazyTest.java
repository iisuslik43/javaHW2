package ru.iisuslik.lazy;

import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.*;

/**
 * Tests to LazyFactory.getLazy(...)
 */
public class LazyTest {
    /**
     * Just creates Lazy
     */
    @Test
    public void createLazy() {
        Lazy<Integer> lazy = LazyFactory.createLazy(() -> 2 + 2);
        assertEquals(4, (int) lazy.get());
        assertEquals(4, (int) lazy.get());
    }

    /**
     * Checks that get form supplier calls only 1 time
     */
    @Test
    public void getHappensOnlyOneTime() {
        Lazy<Integer> strange = LazyFactory.createLazy(strangeSupplier);
        assertEquals(43, (int) strange.get());
        assertEquals(43, (int) strange.get());
    }

    /**
     * Check that we work correct with null returning supplier
     */
    @Test
    public void nullReturnValue() {
        Lazy<Integer> returnsNull = LazyFactory.createLazy(() -> null);
        assertEquals(null, returnsNull.get());
        assertEquals(null, returnsNull.get());
    }

    /**
     * Checks that null returning Lazy will call supplier once
     */
    @Test
    public void nullReturningCallOnes() {
        Lazy<Integer> returnsNull = LazyFactory.createLazy(new Supplier<Integer>() {
            private int count = 0;

            @Override
            public Integer get() {
                count++;
                if (count > 1)
                    return 43;
                return null;
            }
        });
        assertEquals(null, returnsNull.get());
        assertEquals(null, returnsNull.get());
    }


    private Supplier<Integer> strangeSupplier = new Supplier<Integer>() {
        boolean getHappend = false;

        @Override
        public Integer get() {
            if (getHappend)
                return null;
            getHappend = true;
            return 43;
        }
    };
}