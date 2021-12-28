package com.github.dc.im.manager;

import com.github.dc.im.constant.ConstantArgs;
import com.github.dc.im.pojo.NullWebSocketSession;
import com.github.dc.im.pojo.UserInfoData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
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
public class WebSocketSessionManager {
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
            String username = ((UserInfoData) session.getAttributes().get(ConstantArgs.WebSocketSession.USER_INFO)).getUsername();
            List<WebSocketSession> sessions = getAll();
            for (WebSocketSession repeatSession : sessions) {
                UserInfoData user = (UserInfoData) repeatSession.getAttributes().get(ConstantArgs.WebSocketSession.USER_INFO);
                if (user != null && username.equals(user.getUsername())) {
                    removeAndClose((String) session.getAttributes().get(ConstantArgs.WebSocketSession.KEY));
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
                log.warn("[ws关闭异常] 凭证：{}", key);
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
        return SESSION_POOL.get(key) == null ? new NullWebSocketSession(key) : SESSION_POOL.get(key);
    }

    /**
     * 获得 session
     *
     * @param username 用户名
     * @return session
     */
    public static WebSocketSession getByUsername(String username) {
        Objects.requireNonNull(username, "用户名不能为空");
        Map.Entry<String, WebSocketSession> entry = SESSION_POOL.entrySet().stream().filter(u -> username.equals(((UserInfoData) u.getValue().getAttributes().get(ConstantArgs.WebSocketSession.USER_INFO)).getUsername())).findFirst().orElse(null);
        return entry == null ? new NullWebSocketSession(UserInfoData.builder().username(username).build()) : entry.getValue();
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

    /**
     * 根据用户名判断是否在线
     * @param username 用户名
     * @return 是否在线
     */
    public static Boolean isOnline(String username) {
        WebSocketSession session = getByUsername(username);
        return session != null && session.isOpen();
    }
}
