package com.catas.rpc.factory;

import com.google.common.util.concurrent.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.*;

/**
 * 线程池工具类
 */
@Slf4j
public class ThreadPoolFactory {

    private static final int CORE_POOL_SIZE = 5;
    private static final int MAX_POOL_SIZE = 50;
    private static final int KEEP_ALIVE = 60;
    private static final int BLOCK_QUEUE_CAPACITY = 100;

    private static Map<String, ExecutorService> threadPoolsMap = new ConcurrentHashMap<>();

    private ThreadPoolFactory() {
        super();
    }

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix) {
        return createDefaultThreadPool(threadNamePrefix, false);
    }

    public static ExecutorService createDefaultThreadPool(String threadNamePrefix, Boolean daemon) {
        // 如果key对应的value存在，则直接返回value，如果不存在则使用第二个参数（函数）计算的值作为value返回，并保存为该key的value
        ExecutorService pool = threadPoolsMap.computeIfAbsent(threadNamePrefix, k -> createThreadPool(threadNamePrefix, daemon));
        if (pool.isShutdown() || pool.isTerminated()) {
            threadPoolsMap.remove(threadNamePrefix);
            // 重新构建线程池并放入 map
            pool = createThreadPool(threadNamePrefix, daemon);
            threadPoolsMap.put(threadNamePrefix, pool);
        }
        return pool;
    }

    private static ExecutorService createThreadPool(String threadNamePrefix, Boolean daemon) {
        ArrayBlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(BLOCK_QUEUE_CAPACITY);
        ThreadFactory threadFactory = createThreadFactory(threadNamePrefix, daemon);
        return new ThreadPoolExecutor(CORE_POOL_SIZE, MAX_POOL_SIZE, KEEP_ALIVE, TimeUnit.MINUTES, workQueue, threadFactory);
    }

    /**
     *
     * @param threadNamePrefix 线程名前缀
     * @param daemon 是否为守护线程
     */
    private static ThreadFactory createThreadFactory(String threadNamePrefix, Boolean daemon) {
        if (threadNamePrefix != null) {
            if (daemon != null) {
                // 用guava中的ThreadFactoryBuilder自定义创建线程工厂
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").setDaemon(daemon).build();
            }else {
                return new ThreadFactoryBuilder().setNameFormat(threadNamePrefix + "-%d").build();
            }
        }
        return Executors.defaultThreadFactory();
    }

    public static void shutDownAll() {
        log.info("关闭线程池...");

        // 利用parallelStream()并行关闭所有线程池
        threadPoolsMap.entrySet().parallelStream().forEach(entry -> {
            ExecutorService executorService = entry.getValue();
            executorService.shutdown();
            log.info("关闭线程池: [{}] [{}]", entry.getKey(), executorService.isTerminated());
            try {
                executorService.awaitTermination(10, TimeUnit.SECONDS);
            } catch (InterruptedException e) {
                log.info("线程池 {} 关闭失败.", entry.getKey());
                executorService.shutdownNow();
            }
        });

    }
}
