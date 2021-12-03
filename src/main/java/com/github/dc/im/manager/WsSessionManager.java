package com.github.dc.im.manager;

import com.github.dc.im.pojo.UserInfoData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p>
 *     websocket 会话管理
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/10 16:44
 */
@Slf4j
public class WsSessionManager {
    /**
     * 保存连接 session 的地方
     */
    private static ConcurrentHashMap<String, WebSocketSession> SESSION_POOL = new ConcurrentHashMap<>();

    /**
     * 添加 session
     *
     * @param key
     */
    public static void add(String key, WebSocketSession session) {
        // 添加 session
        if (SESSION_POOL.containsKey(key)) {
            removeAndClose(key);
            // 删除该用户其他连接，只允许单个连接
            String username = ((UserInfoData) session.getAttributes().get("userInfo")).getUsername();
            List<WebSocketSession> sessions = getAll();
            for (WebSocketSession repeatSession : sessions) {
                UserInfoData user = (UserInfoData) repeatSession.getAttributes().get("userInfo");
                if (user != null && username.equals(user.getUsername())) {
                    removeAndClose((String) session.getAttributes().get("openId"));
                }
            }
        }
        SESSION_POOL.put(key, session);
    }

    /**
     * 删除 session,会返回删除的 session
     *
     * @param key
     * @return
     */
    public static WebSocketSession remove(String key) {
        // 删除 session
        return SESSION_POOL.remove(key);
    }

    /**
     * 删除并同步关闭连接
     *
     * @param key
     */
    public static void removeAndClose(String key) {
        WebSocketSession session = remove(key);
        if (session != null) {
            try {
                // 关闭连接
                session.close();
            } catch (IOException e) {
                log.warn("[ws关闭异常] {}", key);
            }
        }
    }

    /**
     * 获得 session
     *
     * @param key
     * @return
     */
    public static WebSocketSession get(String key) {
        // 获得 session
        return SESSION_POOL.get(key);
    }

    /**
     * 获得 session
     *
     * @param username 用户名
     * @return session
     */
    public static WebSocketSession getByUsername(String username) {
        Objects.requireNonNull(username, "用户名不能为空");
        return SESSION_POOL.entrySet().stream().filter(u -> username.equals(((UserInfoData) u.getValue().getAttributes().get("userInfo")).getUsername())).findFirst().get().getValue();
    }

    /**
     * 获得 session
     *
     * @return
     */
    public static List<WebSocketSession> getAll() {
        // 获得 session
        return Collections.unmodifiableList(SESSION_POOL.values().stream().collect(Collectors.toList()));
    }
}