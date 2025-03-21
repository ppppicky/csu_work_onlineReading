package org.example.config;

import cn.hutool.core.thread.ThreadFactoryBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Configuration
@EnableAsync
public class ThreadPoolConfig {

    // 通用IO密集型线程池（用于文件上传/下载等操作）
    @Bean("ioThreadPool")
    public Executor ioIntensiveExecutor() {
        int corePoolSize = Runtime.getRuntime().availableProcessors() * 2;
        return new LoggingThreadPoolExecutor(
                corePoolSize,
                corePoolSize * 2,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(400),
                new CustomThreadFactory("io-pool"),
                new ThreadPoolExecutor.AbortPolicy());
    }

    // 过期订单处理线程池
    @Bean("orderThreadPool")
    public Executor orderExecutor() {
        return new LoggingThreadPoolExecutor(
                4,  // 核心线程数
                8,  // 最大线程数
                30L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(200),
                new CustomThreadFactory("order"),
                new ThreadPoolExecutor.DiscardPolicy());
    }
    // 支付回调专用线程池（高优先级）
    @Bean("paymentThreadPool")
    public Executor paymentExecutor() {
        return new LoggingThreadPoolExecutor(
                8,   // 核心线程数（根据压测调整）
                16,  // 最大线程数
                30L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(500), // 队列容量
                new CustomThreadFactory("payment-pool"), // 修正线程名称
                new PaymentRejectedExecutionHandler() // 自定义拒绝策略
        );
    }

    // 用户余额更新线程池（低优先级）
    @Bean("creditUpdateThreadPool")
    public Executor creditUpdateExecutor() {
        return new LoggingThreadPoolExecutor(
                4,
                8,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(200),
                new CustomThreadFactory("credit-update"),
                new ThreadPoolExecutor.DiscardPolicy() // 直接丢弃，避免影响核心任务
        );
    }

    static class LoggingThreadPoolExecutor extends ThreadPoolExecutor {

        public LoggingThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime, TimeUnit unit,
                                         BlockingQueue<Runnable> workQueue, ThreadFactory threadFactory,
                                         RejectedExecutionHandler handler) {
            super(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue, threadFactory, handler);
            log.info("线程池 [{}] 创建成功: 核心线程数={}, 最大线程数={}, 队列容量={}",
                    threadFactory, corePoolSize, maximumPoolSize, workQueue.size());
        }

        @Override
        protected void beforeExecute(Thread t, Runnable r) {
            super.beforeExecute(t, r);
            log.info("线程 [{}] 开始执行任务: {}", t.getName(), r.getClass().getSimpleName());
        }

        @Override
        protected void afterExecute(Runnable r, Throwable t) {
            super.afterExecute(r, t);
            if (t == null) {
                log.info("任务 [{}] 执行完成", r.getClass().getSimpleName());
            } else {
                log.error("任务 [{}] 执行异常: {}", r.getClass().getSimpleName(), t);
            }
        }

        @Override
        public void shutdown() {
            log.info("线程池 [{}] 开始关闭", this);
            super.shutdown();
        }

        @Override
        public List<Runnable> shutdownNow() {
            log.warn("线程池 [{}] 被强制关闭", this);
            return super.shutdownNow();
        }
    }

    // 自定义线程工厂
    static class CustomThreadFactory implements ThreadFactory {
        private final AtomicInteger threadNumber = new AtomicInteger(1);
        private final String namePrefix;

        CustomThreadFactory(String poolName) {
            namePrefix = "app-" + poolName + "-thread-";
        }

        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r, namePrefix + threadNumber.getAndIncrement());
            log.info("线程池 [{}] 创建新线程: {}", namePrefix, thread.getName());
            return thread;
        }
    }

    // 自定义拒绝策略：记录日志并触发告警
    static class PaymentRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            String errorMsg = String.format(
                    "支付线程池满载拒绝任务！活跃线程=%d, 队列大小=%d, 已执行任务=%d",
                    executor.getActiveCount(),
                    executor.getQueue().size(),
                    executor.getCompletedTaskCount()
            );
            log.error(errorMsg);

            // 触发告警（示例：发送HTTP请求到监控系统）


            // 抛出异常让 Controller 层捕获
            throw new RejectedExecutionException(errorMsg);
        }


    }
}