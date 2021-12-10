package com.github.dc.im.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 *     线程池配置
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/10 9:04
 */
@Configuration
public class DcImThreadPoolTaskExecutorConfig {

    @Bean("dcImAsync")
    public ThreadPoolTaskExecutor executor(){
        //
        // IO密集型: 核心线程数 = CPU核数 / （1-阻塞系数）     例如阻塞系数 0.9（阻塞系数在0.8~0.9之间），CPU核数为4，则核心线程数为40
        // ------------------------------------------------------------------------------
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //此方法返回可用处理器的虚拟机的最大数量; 不小于1
        int core = Runtime.getRuntime().availableProcessors();
        //设置核心线程数
        executor.setCorePoolSize(core*10);
        //设置最大线程数
        executor.setMaxPoolSize(core*20);
        //除核心线程外的线程存活时间
        executor.setKeepAliveSeconds(3);
        //如果传入值大于0，底层队列使用的是LinkedBlockingQueue,否则默认使用SynchronousQueue
        executor.setQueueCapacity(40);
        //线程名称前缀
        executor.setThreadNamePrefix("dc-im-async-");
        //设置拒绝策略
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        return executor;
    }
}
