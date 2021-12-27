package com.github.dc.im.send;

import org.springframework.web.socket.WebSocketSession;

import java.util.List;

/**
 * <p>
 *     发送方式
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/24 9:36
 */
public interface ISendMethod<T> {

    /**
     * 群发消息给所有在线用户
     * @param message 消息内容
     */
    void toAll(T message);

    /**
     * 群发消息给所有在线用户，除了指定会话
     * @param message 消息内容
     * @param excludeSession 排除会话
     */
    void toAll(T message, WebSocketSession... excludeSession);

    /**
     * 群发消息给所有在线用户，除了指定会话
     * @param message 消息内容
     * @param excludeSessions 排除会话
     */
    void toAll(T message, List<WebSocketSession> excludeSessions);

    /**
     * 推送消息给指定人员 （多个）
     * @param message 消息内容
     * @param toSession 指定人员会话
     */
    void to(T message, WebSocketSession... toSession);

    /**
     * 推送消息给指定人员（多个）
     * @param message 消息内容
     * @param toSessions 指定人员会话
     */
    void to(T message, List<WebSocketSession> toSessions);

    /**
     * 推送消息给指定凭证会话
     * @param message 消息内容
     * @param key 指定凭证
     */
    void to(T message, String... key);

    /**
     * 推送消息给指定用户
     * @param message 消息内容
     * @param username 指定用户
     */
    void toUser(T message, String... username);

    /**
     * 推送消息给指定用户
     * @param message 消息内容
     * @param usernames 指定用户
     */
    void toUser(T message, List<String> usernames);
}
