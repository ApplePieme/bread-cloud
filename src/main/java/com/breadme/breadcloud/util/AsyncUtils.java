package com.breadme.breadcloud.util;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.*;

/**
 * 异步工具类
 *
 * @author breadme@foxmail.com
 * @date 2022/4/28 17:54
 */
@Slf4j
public class AsyncUtils {
    private AsyncUtils() {

    }

    /**
     * 拒绝策略 - 调用者执行
     */
    private static final RejectedExecutionHandler CALLER = new ThreadPoolExecutor.CallerRunsPolicy();

    /**
     * 拒绝策略 - 忽略
     */
    private static final RejectedExecutionHandler IGNORE = new ThreadPoolExecutor.DiscardPolicy();

    /**
     * 拒绝策略 - 忽略最老的
     */
    private static final RejectedExecutionHandler IGNORE_OLD = new ThreadPoolExecutor.DiscardOldestPolicy();

    /**
     * 拒绝策略 - 抛异常
     */
    private static final RejectedExecutionHandler THROW = new ThreadPoolExecutor.AbortPolicy();

    /**
     * 线程池
     */
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(
            20, 300, 5, TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(1024),
            new RunnablePolicy());

    /**
     * 延迟线程池
     * 拒绝策略：拒绝执行
     */
    private static final ScheduledExecutorService DELAY_EXECUTOR = Executors.newScheduledThreadPool(20);

    /**
     * 延迟执行
     * 拒绝策略 - 调用者执行
     *
     * @param call 执行体
     * @param time 延迟时间
     * @param unit 时间单位
     * @param <V> 泛型
     * @return ScheduledFuture
     */
    public static <V> ScheduledFuture<V> delay(Callable<V> call, long time, TimeUnit unit) {
        return DELAY_EXECUTOR.schedule(Async.get(call), time, unit);
    }

    /**
     * 延迟执行
     * 拒绝策略 - 调用者执行
     *
     * @param run 执行体
     * @param time 延迟时间
     * @param unit 时间单位
     */
    public static void delay(Runnable run, long time, TimeUnit unit) {
        DELAY_EXECUTOR.schedule(Async.get(() -> {
            run.run();
            return true;
        }), time, unit);
    }

    /**
     * 异步执行
     * 拒绝策略 - 调用者执行
     *
     * @param call 执行体
     * @param <T> 泛型
     * @return Future
     */
    public static <T> Future<T> async(Callable<T> call) {
        return EXECUTOR.submit(Async.get(call));
    }

    /**
     * 异步执行
     * 拒绝策略 - 调用者执行
     *
     * @param run 执行体
     */
    public static void async(Runnable run) {
        EXECUTOR.submit(Async.get(() -> {
            run.run();
            return true;
        }));
    }

    /**
     * 异步执行
     * 拒绝策略 - 忽略
     *
     * @param call 执行体
     * @param <T> 泛型
     * @return Future
     */
    public static <T> Future<T> ignore(Callable<T> call) {
        return EXECUTOR.submit(Async.get(call, IGNORE));
    }

    /**
     * 异步执行
     * 拒绝策略 - 忽略
     *
     * @param run 执行体
     */
    public static void ignore(Runnable run) {
        EXECUTOR.submit(Async.get(() -> {
            run.run();
            return true;
        }, IGNORE));
    }

    /**
     * 异步执行
     * 拒绝策略 - 忽略最老的
     *
     * @param call 执行体
     * @param <T> 泛型
     * @return Future
     */
    public static <T> Future<T> ignoreOld(Callable<T> call) {
        return EXECUTOR.submit(Async.get(call, IGNORE_OLD));
    }

    /**
     * 异步执行
     * 拒绝策略 - 忽略最老的
     *
     * @param run 执行体
     */
    public static void ignoreOld(Runnable run) {
        EXECUTOR.submit(Async.get(() -> {
            run.run();
            return true;
        }, IGNORE_OLD));
    }

    /**
     * 异步执行
     * 拒绝策略 - 抛异常
     *
     * @param call 执行体
     * @param <T> 泛型
     * @return Future
     */
    public static <T> Future<T> throwable(Callable<T> call) {
        return EXECUTOR.submit(Async.get(call, THROW));
    }

    /**
     * 异步执行
     * 拒绝策略 - 抛异常
     *
     * @param run 执行体
     */
    public static void throwable(Runnable run) {
        EXECUTOR.submit(Async.get(() -> {
            run.run();
            return true;
        }, THROW));
    }

    /**
     * 自定义拒绝策略
     * 每个执行方法都可以指定不同的拒绝策略
     * 默认拒绝策略 - 调用者执行
     */
    private static class RunnablePolicy implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            if (r instanceof Async) {
                ((Async<?>) r).rejectedExecution(r, executor);
            }
            CALLER.rejectedExecution(r, executor);
        }
    }

    private static class Async<T> implements Callable<T>, RejectedExecutionHandler {
        private final Callable<T> call;

        private final RejectedExecutionHandler rejected;

        private Async(Callable<T> call, RejectedExecutionHandler rejected) {
            this.call = call;
            this.rejected = rejected;
        }

        /**
         * 获取异步执行体
         * 默认拒绝策略 - 调用者执行
         *
         * @param call 执行体
         * @param <T> 泛型
         * @return 自定义异步执行体
         */
        private static <T> Async<T> get(Callable<T> call) {
            return new Async<>(call, CALLER);
        }

        /**
         * 获取异步执行体
         * 可以指定拒绝策略
         *
         * @param call 执行体
         * @param rejected 拒绝策略
         * @param <T> 泛型
         * @return 自定义异步执行体
         */
        private static <T> Async<T> get(Callable<T> call, RejectedExecutionHandler rejected) {
            return new Async<>(call, rejected);
        }

        @Override
        public T call() {
            try {
                return call.call();
            } catch (Exception e) {
                log.error("异步执行失败\n", e);
            }
            return null;
        }

        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            rejected.rejectedExecution(r, executor);
        }
    }
}
