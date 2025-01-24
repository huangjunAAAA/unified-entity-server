package com.zjht.unified.data.common.core.util;

import alluxio.util.executor.UniqueBlockingQueue;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

@Slf4j
public class UniqueTaskThreadPool {

    public UniqueTaskThreadPool() {
        this(1000);
        int concurrent = Runtime.getRuntime().availableProcessors();
        tpe = new ThreadPoolExecutor(concurrent, concurrent, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(concurrent));
    }

    public UniqueTaskThreadPool(int capacity, int concurrent) {
        this(capacity);
        tpe = new ThreadPoolExecutor(concurrent, concurrent, 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(concurrent));
    }

    public UniqueTaskThreadPool(int capacity) {
        wq = new UniqueBlockingQueue<>(capacity);
    }

    private UniqueBlockingQueue<TagRunnable> wq;

    private ThreadPoolExecutor tpe = new ThreadPoolExecutor(Runtime.getRuntime().availableProcessors(), Runtime.getRuntime().availableProcessors(), 10, TimeUnit.SECONDS, new ArrayBlockingQueue<Runnable>(Runtime.getRuntime().availableProcessors()));

    private ConcurrentHashMap.KeySetView<String, Boolean> running = ConcurrentHashMap.newKeySet();

    public interface TagRunnable extends Runnable {
        String getTag();
    }

    public boolean submit(TagRunnable r) {
        if (tpe.getActiveCount() == 0) {
            synchronized (tpe) {
                if (tpe.getActiveCount() == 0) {
                    for (int i = 0; i < Runtime.getRuntime().availableProcessors(); i++) {
                        tpe.submit(new Runnable() {
                            @Override
                            public void run() {
                                while (!Thread.currentThread().isInterrupted())
                                    try {
                                        TagRunnable tsk = wq.take();
                                        if (running.add(tsk.getTag()))
                                            try {
                                                tsk.run();
                                            } finally {
                                                running.remove(tsk.getTag());
                                            }
                                    } catch (Exception e) {
                                        log.error(e.getMessage(), e);
                                    }
                            }
                        });
                    }
                }
            }
        }
        try {
            return wq.offer(r, 10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            log.error(e.getMessage(), e);
        }
        return false;
    }


}
