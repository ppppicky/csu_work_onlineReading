package org.example.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Configuration
@EnableAsync
public class ThreadPoolConfig {

    // 通用IO密集型线程池（用于文件上传/下载等操作）
    @Bean("ioThreadPool")
    public Executor ioIntensiveExecutor() {
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
        return new ThreadPoolExecutor(
                corePoolSize,
                corePoolSize * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(100),
                new CustomThreadFactory("io-pool"),
                new ThreadPoolExecutor.CallerRunsPolicy());
    }

    // 支付回调处理线程池
    @Bean("paymentCallbackPool")
    public Executor paymentCallbackExecutor() {
        return new ThreadPoolExecutor(
                4,
                8,
                30L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(200),
                new CustomThreadFactory("payment-callback"),
                new ThreadPoolExecutor.AbortPolicy());
    }

    // 自定义线程工厂
    static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        CustomThreadFactory(String poolName) {
            namePrefix = "app-" + poolName + "-thread-";
        }

        public Thread newThread(Runnable r) {
            return new Thread(r, namePrefix + threadNumber.getAndIncrement());
        }
    }
}