package com.github.dc.im.config;

import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * <p>
 *     计划任务配置
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/10 11:13
 */
@EnableScheduling
@Configuration
public class DcImSchedulingConfig {

    @Bean
    @ConditionalOnMissingBean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
        scheduler.setPoolSize(2);
        scheduler.initialize();
        return scheduler;
    }
}
