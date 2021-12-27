package com.github.dc.im.send;

import com.alibaba.fastjson.JSON;
import com.github.dc.im.handler.CustomTextMessageHandler;
import com.github.dc.im.handler.MessageRecordAsyncHandler;
import com.github.dc.im.helper.ApplicationContextHelper;
import com.github.dc.im.manager.WebSocketSessionManager;
import com.github.dc.im.pojo.ChatContentData;
import com.github.dc.im.pojo.UserInfoData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;

import java.io.IOException;
import java.util.Date;

/**
 * <p>
 *     聊天内容发送器
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/24 11:32
 */
@Slf4j
public class ChatMessageSender {

    /**
     * 来源用户名发送聊天消息到目标用户名
     * @param content 聊天内容
     * @param fromUsername 来源用户名
     * @param toUsername 目标用户名
     */
    public static void to(String content, String fromUsername, String toUsername) {
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
    public static void to(String content, WebSocketSession from, String toUsername) {
        WebSocketSession to = WebSocketSessionManager.getByUsername(toUsername);
        to(content, from, to);
    }

    /**
     * 来源用户名发送聊天消息到目标会话
     * @param content 聊天内容
     * @param fromUsername 来源用户名
     * @param to 目标会话
     */
    public static void to(String content, String fromUsername, WebSocketSession to) {
        WebSocketSession from = WebSocketSessionManager.getByUsername(fromUsername);
        to(content, from, to);
    }

    /**
     * 来源会话发送聊天消息到目标会话
     * @param content 聊天内容
     * @param from 来源会话
     * @param to 目标会话
     */
    public static void to(String content, WebSocketSession from, WebSocketSession to) {
        UserInfoData fromUser = (UserInfoData) from.getAttributes().get("userInfo");
        if (to == null || !to.isOpen()) {
            // TODO: 2021/11/11 离线消息记录后上线推送
            log.warn("[发送失败] 客户端未连接，发送者：{}，内容：{}", fromUser, content);
            return;
        }
        UserInfoData toUser = (UserInfoData) to.getAttributes().get("userInfo");
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
            from.sendMessage(new TextMessage(JSON.toJSONString(chatContentData)));
            to.sendMessage(new TextMessage(JSON.toJSONString(chatContentData.toBuilder().self(false).build())));
        } catch (IOException e) {
            log.warn("[发送失败] 聊天记录发送失败，IO异常", e);
        } finally {
            MessageRecordAsyncHandler messageHandler = ApplicationContextHelper.getBean(MessageRecordAsyncHandler.class);
            messageHandler.recordData(chatContentData);
        }
    }
}
