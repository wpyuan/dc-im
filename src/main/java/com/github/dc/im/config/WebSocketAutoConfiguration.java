package com.github.dc.im.config;

import com.github.dc.im.handler.CustomTextMessageHandler;
import com.github.dc.im.interceptor.WebSocketInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
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

    @Autowired
    private WebSocketInterceptor webSocketInterceptor;

    /**
     * ws://127.0.0.1:8080/ws
     * @param registry
     */
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(new CustomTextMessageHandler(), "ws")
                .addInterceptors(webSocketInterceptor)
                .setAllowedOrigins("*");
    }
}
