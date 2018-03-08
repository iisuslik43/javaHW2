package ru.iisuslik;

import org.junit.Test;

import java.util.function.Supplier;

import static org.junit.Assert.*;

/**
 * Tests for ThreadPoolmpl class
 */
public class ThreadPoolmplTest {

    /**
     * Some simple test for most of the functionality
     */
    @Test
    public void simpleTest() throws Exception {
        ThreadPoolmpl<Integer> pool = new ThreadPoolmpl<>(5);
        LightFuture<Integer> task = pool.addTask(() -> 2 * 2);
        assertEquals(4, (int) task.get());
        LightFuture<Integer> task1 = pool.addTask(() -> 2 * 3);
        LightFuture<Integer> task2 = task1.thenApply((i) -> i + 1);
        LightFuture<Integer> task3 = task1.thenApply((i) -> i + 2);
        assertEquals(6, (int) task1.get());
        assertEquals(7, (int) task2.get());
        assertEquals(8, (int) task3.get());
    }

    /**
     * Calculates one task
     */
    @Test
    public void oneTask() throws Exception {
        ThreadPoolmpl<Integer> pool = new ThreadPoolmpl<>(2);
        LightFuture<Integer> task = pool.addTask(() -> 43);
        assertEquals(43, (int) task.get());
        assertTrue(task.isReady());
        pool.shutDown();
    }

    /**
     * Calculates 2 tasks
     */
    @Test
    public void twoTasks() throws Exception {
        ThreadPoolmpl<Integer> pool = new ThreadPoolmpl<>(2);
        LightFuture<Integer> task1 = pool.addTask(() -> 43);
        LightFuture<Integer> task2 = pool.addTask(() -> 42);
        assertEquals(43, (int) task1.get());
        assertEquals(42, (int) task2.get());
        assertTrue(task1.isReady());
        assertTrue(task2.isReady());
        pool.shutDown();
    }

    /**
     * Checks that if supplier throws some exception, get will throw LightExecutionException
     */
    @Test(expected = LightExecutionException.class)
    public void taskWithException() throws Exception {
        ThreadPoolmpl<Integer> pool = new ThreadPoolmpl<>(5);
        LightFuture<Integer> task = pool.addTask(taskThrowsNullPointerException);
        task.get();
    }

    /**
     * Checks that supplier's exception will be added as suppressed to LightExecutionException
     */
    @Test
    public void taskWithExceptionCheckSuppressed() {
        ThreadPoolmpl<Integer> pool = new ThreadPoolmpl<>(5);
        LightFuture<Integer> task = pool.addTask(taskThrowsNullPointerException);
        try {
            task.get();
        } catch (LightExecutionException e) {
            assertEquals(NullPointerException.class.getName(), e.getSuppressed()[0].getClass().getName());
        }
    }

    /**
     * Calculates task that has previous task's result as argument
     */
    @Test
    public void thenApplyTest() throws Exception {
        ThreadPoolmpl<Integer> pool = new ThreadPoolmpl<>(2);
        LightFuture<Integer> task = pool.addTask(() -> 43);
        LightFuture<Integer> functionTask = task.thenApply(a -> a + 4);
        assertEquals(47, (int) functionTask.get());
        assertTrue(task.isReady());
        assertTrue(functionTask.isReady());

        pool.shutDown();
    }

    /**
     * Calculates 2 tasks that both was returned by thenApply(...) from one task
     */
    @Test
    public void twoThenApplyTasks() throws Exception {
        ThreadPoolmpl<Integer> pool = new ThreadPoolmpl<>(2);
        LightFuture<Integer> task = pool.addTask(() -> 43);
        LightFuture<Integer> task1 = task.thenApply(a -> a * 2);
        LightFuture<Integer> task2 = task.thenApply(a -> a + 2);
        assertEquals(45, (int) task2.get());
        assertTrue(task.isReady());
        assertEquals(86, (int) task1.get());
        assertTrue(task1.isReady());
        assertTrue(task2.isReady());
        pool.shutDown();
    }

    /**
     * Checks that if task hasn't finished, method get waits as long as he has to
     */
    @Test
    public void getReallyWaits() throws Exception {
        ThreadPoolmpl<Integer> pool = new ThreadPoolmpl<>(2);
        LightFuture<Integer> task = pool.addTask(sleepingTask);
        assertEquals(43, (int) task.get());
        assertTrue(task.isReady());
        pool.shutDown();
    }

    /**
     * Checks that thenApply waits for result too
     */
    @Test
    public void thenApplyReallyWaits() throws Exception {
        ThreadPoolmpl<Integer> pool = new ThreadPoolmpl<>(2);
        LightFuture<Integer> task = pool.addTask(sleepingTask);
        LightFuture<Integer> functionTask = task.thenApply(a -> a + 1);
        assertEquals(44, (int) functionTask.get());
        assertTrue(task.isReady());
        pool.shutDown();
    }

    /**
     * Checks that if task from thenApply throws some exception, get will throw LightExecutionException
     */
    @Test(expected = LightExecutionException.class)
    public void exceptionInThenApplyThrows() throws Exception {
        ThreadPoolmpl<Integer> pool = new ThreadPoolmpl<>(5);
        LightFuture<Integer> task = pool.addTask(() -> 43);
        LightFuture<Integer> task2 = task.thenApply(a -> {
            int[] arr = new int[43];
            return arr[43];
        });
        task2.get();
    }

    /**
     * Checks that this exception will be added as suppressed to LightExecutionException
     */
    @Test
    public void exceptionInThenApplyCheckSuppressed() {
        ThreadPoolmpl<Integer> pool = new ThreadPoolmpl<>(5);
        LightFuture<Integer> task = pool.addTask(() -> 43);
        LightFuture<Integer> task2 = task.thenApply(a -> {
            int[] arr = new int[43];
            return arr[43];
        });
        try {
            task2.get();
        } catch (LightExecutionException e) {
            assertEquals(ArrayIndexOutOfBoundsException.class.getName(), e.getSuppressed()[0].getClass().getName());
        }
    }

    private Supplier<Integer> taskThrowsNullPointerException = () -> {
        String nullString = null;
        return nullString.length();
    };

    private Supplier<Integer> taskThrowsArrayIndexOutOfBoundsException = () -> {
        int[] a = new int[43];
        return a[43];
    };

    private Supplier<Integer> sleepingTask = () -> {
        try {
            Thread.sleep(100);
        } catch (InterruptedException ignored) {
        }
        return 43;
    };
}