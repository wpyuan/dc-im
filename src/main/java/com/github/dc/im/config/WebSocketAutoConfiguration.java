package com.github.dc.im.config;

import com.github.dc.im.handler.CustomTextMessageHandler;
import com.github.dc.im.helper.ApplicationContextHelper;
import com.github.dc.im.interceptor.WebSocketInterceptor;
import com.github.dc.im.handler.WebSocketAuthHandler;
import com.github.dc.im.handler.DefaultSocketAuthHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.socket.config.annotation.EnableWebSocket;
import org.springframework.web.socket.config.annotation.WebSocketConfigurer;
import org.springframework.web.socket.config.annotation.WebSocketHandlerRegistry;

/**
 * <p>
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/10 16:53
 */
@Configuration("dcImWebSocketAutoConfiguration")
@EnableWebSocket
@EnableAsync(proxyTargetClass = true)
public class WebSocketAutoConfiguration implements WebSocketConfigurer {

//    @Autowired
//    private CustomTextMessageHandler customTextMessageHandler;
    @Autowired
    private WebSocketInterceptor webSocketInterceptor;

    /**
     * ws://127.0.0.1:8080/ws
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(customTextMessageHandler(), "ws")
                .addInterceptors(webSocketInterceptor)
                .setAllowedOrigins("*");
    }

    /**
     * 客制化开发时需实现
     * @return
     */
    @Bean("webSocketAuthService")
    @ConditionalOnMissingBean(name ="webSocketAuthService")
    public WebSocketAuthHandler webSocketAuthService() {
        return new DefaultSocketAuthHandler();
    }

    @Bean("dcImTextMessageHandler")
    public CustomTextMessageHandler customTextMessageHandler() {
        return new CustomTextMessageHandler();
    }

    @Bean("dcImApplicationContextHelper")
    public ApplicationContextHelper applicationContextHelper() {
        return new ApplicationContextHelper();
    }


}
