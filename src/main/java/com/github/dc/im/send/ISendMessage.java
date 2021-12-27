package com.github.dc.im.send;

import org.springframework.web.socket.WebSocketSession;

/**
 * <p>
 * 发送消息
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/24 9:36
 */
public interface ISendMessage<T> {

    /**
     * 处理发送
     * @param message 消息内容
     * @param session 会话
     */
    void handlerSend(T message, WebSocketSession session);

    /**
     * 处理发送异常
     * @param message 消息内容
     * @param session 会话
     * @param e 异常
     */
    void handlerException(T message, WebSocketSession session, Exception e);
}
