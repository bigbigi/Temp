package com.reomote.carcontroller.utils;

import android.os.Handler;
import android.os.Looper;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Created by big on 2018/11/8.
 */

public class ThreadManager {
    private static final int MAX_SIZE = 5;
    private static ExecutorService mExecutor; // 会自动回收的无界限线程池
    private static ExecutorService mSingleExecutor; // 单线程
    private static LinkedBlockingQueue mSingleQueue = new LinkedBlockingQueue();

    /**
     * @param @param run 设定文件
     * @return void 返回类型
     * @throws
     * @Description: TODO(耗时任务的执行入口函數)
     */
    public static void execute(Runnable task) {
        if (task == null) {
            return;
        }
        getExecutor().execute(task);
    }

    public static void single(Runnable task) {
        if (task == null) {
            return;
        }
        getSingleExecutor().execute(task);
    }

    public static void clearSinglTask() {
        mSingleQueue.clear();
    }

    /**
     * 获取一个单例会自动回收的无界线程池
     */
    private static synchronized ExecutorService getExecutor() {
        if (mExecutor == null) {
            mExecutor = new ThreadPoolExecutor(MAX_SIZE, MAX_SIZE, 0L, TimeUnit.MILLISECONDS,
                    new LinkedBlockingQueue(), createThreadFactory(10, "ThreadManager-"));
        }
        return mExecutor;
    }

    /**
     * 获取一个单例会自动回收的无界线程池
     */
    private static synchronized ExecutorService getSingleExecutor() {
        if (mSingleExecutor == null) {
            mSingleExecutor = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
                    mSingleQueue, createThreadFactory(10, "SingleThread-"));
        }
        return mSingleExecutor;
    }


    private static ThreadFactory createThreadFactory(int threadPriority, String threadNamePrefix) {
        return new DefaultThreadFactory(threadPriority, threadNamePrefix);
    }

    private static class DefaultThreadFactory implements ThreadFactory {
        private static final AtomicInteger poolNumber = new AtomicInteger(1);
        private final ThreadGroup group;
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;
        private final int threadPriority;

        DefaultThreadFactory(int threadPriority, String threadNamePrefix) {
            this.threadPriority = threadPriority;
            this.group = Thread.currentThread().getThreadGroup();
            this.namePrefix = threadNamePrefix + poolNumber.getAndIncrement() + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread t = new Thread(this.group, r, this.namePrefix + this.threadNumber.getAndIncrement(), 0L);
            if (t.isDaemon()) {
                t.setDaemon(false);
            }

            t.setPriority(this.threadPriority);
            return t;
        }
    }


    private static Handler uiHandler = new Handler(Looper.getMainLooper());

    public static void runOnUiThread(Runnable runnable) {
        if (runnable == null) {
            return;
        }
        if (Thread.currentThread() == Looper.getMainLooper().getThread()) {
            runnable.run();
        } else {
            uiHandler.post(runnable);
        }
    }
}
