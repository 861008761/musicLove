package com.mylovin.music.util.threadPool;

import org.apache.commons.lang3.concurrent.BasicThreadFactory;

import java.util.concurrent.*;

/**
 * 线程池帮助类
 */
public class ThreadPoolUtil {
    private static final ThreadPoolExecutor EXECUTOR_SERVICE = new ThreadPoolExecutor(5, 100, 60, TimeUnit.SECONDS, new ArrayBlockingQueue<>(100),
            new BasicThreadFactory.Builder().namingPattern("schedule-pool-%d").daemon(true).build()
            , new ThreadPoolExecutor.CallerRunsPolicy());

    public static void execute(Runnable runnable) {
        EXECUTOR_SERVICE.execute(runnable);
    }

    public static Future submit(Runnable runnable) {
        return EXECUTOR_SERVICE.submit(runnable);
    }

    public static Future submit(Callable callable) {
        return EXECUTOR_SERVICE.submit(callable);
    }
}
