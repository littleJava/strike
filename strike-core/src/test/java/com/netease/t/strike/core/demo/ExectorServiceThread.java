package com.netease.t.strike.core.demo;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.junit.Assert;

public class ExectorServiceThread extends Assert {
    // @Test
    public static void main(String[] args) {
//        new ExectorServiceThread().threadCount();
        new ExectorServiceThread().threadpoolQueue();
    }

    public void threadCount() {
        // ExecutorService service = Executors.newCachedThreadPool();
        final CountDownLatch latch = new CountDownLatch(3);
        ThreadPoolExecutor pool = (ThreadPoolExecutor) Executors.newCachedThreadPool();
        for (int i = 0; i < 3; i++) {
            System.out.println("latch:" + latch.getCount());
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(3);
                    } catch (InterruptedException e) {
                    }
                    System.out.println(Thread.currentThread().getName());
                    latch.countDown();
                }
            });
        }
        System.out.println("latch:" + latch.getCount());
        System.out.println("getActiveCount:" + pool.getActiveCount());
        System.out.println("getCompletedTaskCount:" + pool.getCompletedTaskCount());

        try {
            pool.setKeepAliveTime(0, TimeUnit.MILLISECONDS);// 在latch后面会导致线程无法移除
            latch.await();
            // TimeUnit.SECONDS.sleep(1);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        System.out.println("-------------------------------");
        for (int i = 0; i < 3; i++) {
            System.out.println("latch:" + latch.getCount());
            pool.submit(new Runnable() {
                @Override
                public void run() {
                    try {
                        TimeUnit.SECONDS.sleep(5);
                    } catch (InterruptedException e) {
                    }
                    System.out.println(Thread.currentThread().getName());
                    // latch.countDown();
                }
            });
        }
        System.out.println("latch:" + latch.getCount());
        System.out.println("getActiveCount:" + pool.getActiveCount());
        System.out.println("getCompletedTaskCount:" + pool.getCompletedTaskCount());
        System.out.println("ExectorServiceThread.threadCount()");
        pool.shutdown();
    }

    public void threadpoolQueue() {
        LinkedBlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<Runnable>();
        final CountDownLatch latch = new CountDownLatch(5);
        Callable<Boolean> callable = new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                try {
                    TimeUnit.SECONDS.sleep(5);
                } catch (InterruptedException e) {
                }
                System.out.println(Thread.currentThread().getName());
                latch.countDown();
                return true;
            }
        };
        for (int i = 0; i < 5; i++) {
            workQueue.add(new FutureTask<Boolean>(callable));
        }
        ThreadPoolExecutor pool = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS, workQueue);
        System.out.println("getActiveCount:" + pool.getActiveCount());
        System.out.println("getCompletedTaskCount:" + pool.getCompletedTaskCount());
        
     // pool.invokeAll(workQueue);
        pool.submit(new Callable<Boolean>() {

            @Override
            public Boolean call() throws Exception {
                System.out.println("ExectorServiceThread.threadpoolQueue().new Callable<Boolean>() {...}.call()");
                return true;
            }
        });
        
        
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Queue<Callable<Boolean>> queue = new ArrayBlockingQueue<Callable<Boolean>>(8);
        try {
            pool.invokeAll(queue);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        System.out.println("getActiveCount:" + pool.getActiveCount());
        System.out.println("getCompletedTaskCount:" + pool.getCompletedTaskCount());
        pool.shutdown();
    }

}
