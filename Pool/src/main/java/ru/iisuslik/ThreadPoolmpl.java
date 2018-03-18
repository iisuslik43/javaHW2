package ru.iisuslik;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Realization of standard thread pool with constant count of threads.
 * Threads can calculate suppliers and functions, you can get result and do some other things
 * by calling methods in LightFuture. LightFuture is special interface that returns when you add tasks.
 *
 * @param <T> Suppliers and Functions returning type:
 *            Supplier: () -> T
 *            Function: T -> T
 */
public class ThreadPoolmpl<T> {

    private Thread[] threads;

    private ArrayList<Task> tasksQueue = new ArrayList<>();

    /**
     * Construct new ThreadPool an start constant count of threads
     *
     * @param threadCount Count of threads that will be running
     */
    public ThreadPoolmpl(int threadCount) {
        threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(new RunnableForThreads(), "Thread " + i);
            threads[i].start();
        }
    }

    /**
     * Add Supplier to the task queue, it will be calculated as soon as some threads will be free
     *
     * @param task Supplier: () -> T that will be calculated in thread pool
     * @return Special class that implements LightFuture, from it you can check that task is finished
     * or get task's result
     */
    public synchronized LightFuture<T> addTask(@NotNull Supplier<T> task) {
        SupplierTask newTask = new SupplierTask(task);
        addTask(newTask);
        return newTask;
    }

    /**
     * Interrupt all the threads in pool, this will stop them
     */
    public void shutDown() {
        for (Thread t : threads) {
            t.interrupt();
        }
    }

    private synchronized void addTask(Task task) {
        tasksQueue.add(task);
        this.notify();
    }

    private class RunnableForThreads implements Runnable {
        @Override
        public void run() {
            try {
                while (true) {
                    synchronized (ThreadPoolmpl.this) {
                        while (tasksQueue.isEmpty()) {
                            ThreadPoolmpl.this.wait();
                        }
                        Task task = tasksQueue.get(0);
                        tasksQueue.remove(0);
                        task.runTask();
                    }
                }
            } catch (InterruptedException ignored) {
            }
        }
    }

    private abstract class Task implements LightFuture<T> {
        boolean isReady = false;
        T result;
        Exception getException = null;

        /**
         * {@link LightFuture<T>#isReady()}
         */
        @Override
        public boolean isReady() {
            return isReady;
        }

        /**
         * {@link LightFuture<T>#get()}
         */
        @Override
        public synchronized T get() throws LightExecutionException {
            try {
                while (!isReady()) {
                    this.wait();
                }
            } catch (InterruptedException ignored) {
            }
            if (getException != null) {
                LightExecutionException e = new LightExecutionException();
                e.addSuppressed(getException);
                throw e;
            }
            return result;
        }

        /**
         * {@link LightFuture<T>#thenApply(Function)}
         */
        @Override
        public LightFuture<T> thenApply(@NotNull Function<T, T> func) {
            Task task = new FunctionTask(this, func);
            addTask(task);
            return task;
        }

        public abstract void runTask();

    }

    private class SupplierTask extends Task {
        private Supplier<T> func;

        private SupplierTask(@NotNull Supplier<T> func) {
            this.func = func;
        }

        @Override
        public synchronized void runTask() {
            try {
                result = func.get();
            } catch (Exception e) {
                getException = e;
            }
            isReady = true;
            notifyAll();
        }
    }

    private class FunctionTask extends Task {
        private final Task parent;
        private final Function<T, T> func;

        @Override
        public synchronized void runTask() {
            try {
                synchronized (parent) {
                    while (!parent.isReady()) {
                        wait();
                    }
                    try {
                        result = func.apply(parent.get());
                    } catch (Exception e) {
                        getException = e;
                    }
                    isReady = true;
                    notifyAll();
                }
            } catch (InterruptedException ignored) {
            }
        }

        private FunctionTask(@NotNull Task parent, @NotNull Function<T, T> func) {
            this.func = func;
            this.parent = parent;
        }
    }
}
