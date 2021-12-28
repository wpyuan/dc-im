package com.github.dc.im.config;

import com.github.dc.im.helper.ApplicationContextHelper;
import com.github.dc.im.pojo.DcImApplicationContext;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * <p>
 *     dc-im配置
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/28 8:57
 */
@Configuration
public class DcImConfig {

    @Bean
    @ConditionalOnMissingBean
    public DcImApplicationContext dcImApplicationContext() {
        return new DcImApplicationContext();
    }

    @Bean("dcImApplicationContextHelper")
    public ApplicationContextHelper applicationContextHelper() {
        return new ApplicationContextHelper();
    }
}
