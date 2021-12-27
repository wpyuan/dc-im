package com.github.dc.im.interceptor;

import com.github.dc.im.manager.AuthenticateUserInfoManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketHandler;
import org.springframework.web.socket.server.HandshakeInterceptor;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/10 16:48
 */
@Component
@Slf4j
public class WebSocketInterceptor implements HandshakeInterceptor {

    @Autowired
    private AuthenticateUserInfoManager authenticateUserInfoManager;

    /**
     * 握手前
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param attributes
     * @return
     * @throws Exception
     */
    @Override
    public boolean beforeHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Map<String, Object> attributes) throws Exception {
        log.trace("握手开始");
        String query = request.getURI().getQuery();
        String[] args = query.split("&");
        Map<String, String> paramMap = new HashMap<>();
        for (String arg : args) {
            paramMap.put(arg.split("=")[0], arg.split("=")[1]);
        }
        String openId = paramMap.get("k");
        if (StringUtils.isBlank(openId) || !authenticateUserInfoManager.containsKey(openId)) {
            log.warn("用户登录已失效，凭证：{}", openId);
            return false;
        }

        // 放入属性域
        attributes.put("openId", openId);
        attributes.put("userInfo", authenticateUserInfoManager.get(openId));
        log.info("[验证通过]: 凭证：{}，用户：{}", openId, authenticateUserInfoManager.get(openId));
        return true;
    }

    /**
     * 握手后
     *
     * @param request
     * @param response
     * @param wsHandler
     * @param exception
     */
    @Override
    public void afterHandshake(ServerHttpRequest request, ServerHttpResponse response, WebSocketHandler wsHandler, Exception exception) {
        log.trace("握手完成");
    }
}
