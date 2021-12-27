package com.github.dc.im.send;

import com.alibaba.fastjson.JSON;
import com.github.dc.im.manager.WebSocketSessionManager;
import com.github.dc.im.pojo.ServerMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * <p>
 * 服务端消息发送器
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/24 9:52
 */
@Slf4j
public class ServerMessageSender implements ISendMessage<ServerMessage>, ISendMethod<ServerMessage> {

    public static final ServerMessageSender INSTANCE = new ServerMessageSender();

    @Override
    public void handlerSend(ServerMessage message, WebSocketSession session) {
        Objects.requireNonNull(session, "[发送失败] 用户未连接");
        if (!session.isOpen()) {
            // TODO: 2021/11/11 离线消息记录后上线推送
            log.warn("[发送失败] 会话已关闭，无法推送服务端消息，凭证：{}，用户：{}，内容：{}", session.getAttributes().get("openId"), session.getAttributes().get("userInfo"), message);
            return;
        }
        try {
            session.sendMessage(new TextMessage(JSON.toJSONString(message)));
        } catch (Exception e) {
            this.handlerException(message, session, e);
        }
    }

    @Override
    public void handlerException(ServerMessage message, WebSocketSession session, Exception e) {
        // TODO 消息补偿机制实现
        log.warn("[发送失败] 服务端发送消息失败，凭证：{}，用户：{}，内容：{}，异常：{}", session.getAttributes().get("openId"), session.getAttributes().get("userInfo"), message, e.getMessage());
    }

    @Override
    public void toAll(ServerMessage message) {
        List<WebSocketSession> allSession = WebSocketSessionManager.getAll();
        allSession.forEach(s -> this.handlerSend(message, s));
    }

    @Override
    public void toAll(ServerMessage message, WebSocketSession... excludeSession) {
        this.toAll(message, Arrays.asList(excludeSession));
    }

    @Override
    public void toAll(ServerMessage message, List<WebSocketSession> excludeSessions) {
        List<WebSocketSession> allSession = WebSocketSessionManager.getAll();
        allSession.forEach(s -> {
            if (!excludeSessions.contains(s)) {
                this.handlerSend(message, s);
            }
        });
    }

    @Override
    public void to(ServerMessage message, WebSocketSession... toSession) {
        this.to(message, Arrays.asList(toSession));
    }

    @Override
    public void to(ServerMessage message, List<WebSocketSession> toSessions) {
        toSessions.forEach(s -> this.handlerSend(message, s));
    }

    @Override
    public void to(ServerMessage message, String... key) {
        Arrays.asList(key).forEach(k -> this.handlerSend(message, WebSocketSessionManager.get(k)));
    }

    @Override
    public void toUser(ServerMessage message, String... username) {
        Arrays.asList(username).forEach(u -> this.handlerSend(message, WebSocketSessionManager.getByUsername(u)));
    }

    @Override
    public void toUser(ServerMessage message, List<String> usernames) {
        usernames.forEach(u -> this.handlerSend(message, WebSocketSessionManager.getByUsername(u)));
    }
}
