package com.github.dc.im.send;

import org.springframework.web.socket.WebSocketSession;

/**
 * <p>
 * 离线消息发送器
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/28 8:15
 */
public interface OfflineMessageSender<T> extends ISendMessage<T> {

    /**
     * 默认不处理发送异常
     * @param message 消息内容
     * @param session 会话
     * @param e 异常
     */
    @Override
    default void handlerException(T message, WebSocketSession session, Exception e) {
    }
}
