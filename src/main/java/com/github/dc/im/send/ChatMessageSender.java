package com.github.dc.im.send;

import com.alibaba.fastjson.JSON;
import com.github.dc.im.constant.ConstantArgs;
import com.github.dc.im.handler.MessageRecordAsyncHandler;
import com.github.dc.im.helper.ApplicationContextHelper;
import com.github.dc.im.manager.WebSocketSessionManager;
import com.github.dc.im.pojo.ChatContentData;
import com.github.dc.im.pojo.DcImApplicationContext;
import com.github.dc.im.pojo.ServerMessage;
import com.github.dc.im.pojo.UserInfoData;
import com.github.dc.im.util.EntityUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.util.Date;
import java.util.Objects;

/**
 * <p>
 * 聊天内容发送器
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/24 11:32
 */
@Slf4j
public class ChatMessageSender implements ISendMessage<ChatContentData> {

    public static final ChatMessageSender INSTANCE = new ChatMessageSender();

    @Override
    public void handlerSend(ChatContentData message, WebSocketSession session) {
        Objects.requireNonNull(session, "[发送失败] 用户未连接");
        if (!session.isOpen()) {
            // TODO: 2021/11/11 离线消息记录后上线推送
            log.warn("[发送失败] 会话已关闭，无法发送聊天内容，转入离线消息处理，凭证：{}，用户：{}，内容：{}", session.getAttributes().get(ConstantArgs.WebSocketSession.KEY), session.getAttributes().get(ConstantArgs.WebSocketSession.USER_INFO), message);
            OfflineMessageSender<ChatContentData> offlineMessageSender = ApplicationContextHelper.getBean(DcImApplicationContext.class).getOfflineMessageSender();
            offlineMessageSender.handlerSend(message, session);
            return;
        }
        try {
            session.sendMessage(new TextMessage(JSON.toJSONString(message)));
        } catch (Exception e) {
            this.handlerException(message, session, e);
        }
    }

    @Override
    public void handlerException(ChatContentData message, WebSocketSession session, Exception e) {
        // TODO 消息补偿机制实现
        log.warn("[发送失败] 发送聊天内容失败，凭证：{}，用户：{}，内容：{}，异常：{}", session.getAttributes().get(ConstantArgs.WebSocketSession.KEY), session.getAttributes().get(ConstantArgs.WebSocketSession.USER_INFO), message, e.getMessage());
    }

    /**
     * 来源会话发送聊天消息到目标会话
     * @param content 聊天内容
     * @param from 来源会话
     * @param to 目标会话
     */
    public void to(String content, WebSocketSession from, WebSocketSession to) {
        UserInfoData fromUser = (UserInfoData) from.getAttributes().get(ConstantArgs.WebSocketSession.USER_INFO);
        UserInfoData toUser = (UserInfoData) to.getAttributes().get(ConstantArgs.WebSocketSession.USER_INFO);
        Date sendDateTime = new Date();
        ChatContentData chatContentData = ChatContentData.builder()
                .date(sendDateTime)
                .content(content)
                .from(fromUser)
                .to(toUser)
                .self(true)
                .uid(sendDateTime.getTime())
                .build();
        try {
            this.handlerSend(chatContentData, from);
            this.handlerSend(chatContentData.toBuilder().self(false).build(), to);
        } finally {
            MessageRecordAsyncHandler messageHandler = ApplicationContextHelper.getBean(MessageRecordAsyncHandler.class);
            messageHandler.recordData(chatContentData);
        }
    }

    /**
     * 来源用户名发送聊天消息到目标用户名
     * @param content 聊天内容
     * @param fromUsername 来源用户名
     * @param toUsername 目标用户名
     */
    public void to(String content, String fromUsername, String toUsername) {
        WebSocketSession from = WebSocketSessionManager.getByUsername(fromUsername);
        WebSocketSession to = WebSocketSessionManager.getByUsername(toUsername);
        to(content, from, to);
    }

    /**
     * 来源会话发送聊天消息到目标用户名
     * @param content 聊天内容
     * @param from 来源会话
     * @param toUsername 目标用户名
     */
    public void to(String content, WebSocketSession from, String toUsername) {
        WebSocketSession to = WebSocketSessionManager.getByUsername(toUsername);
        to(content, from, to);
    }

    /**
     * 来源用户名发送聊天消息到目标会话
     * @param content 聊天内容
     * @param fromUsername 来源用户名
     * @param to 目标会话
     */
    public void to(String content, String fromUsername, WebSocketSession to) {
        WebSocketSession from = WebSocketSessionManager.getByUsername(fromUsername);
        to(content, from, to);
    }
}
