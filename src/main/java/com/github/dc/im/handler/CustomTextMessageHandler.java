package com.github.dc.im.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.dc.im.data.CacheData;
import com.github.dc.im.helper.ApplicationContextHelper;
import com.github.dc.im.manager.WsSessionManager;
import com.github.dc.im.pojo.ChatContentData;
import com.github.dc.im.pojo.OfflineUserInfo;
import com.github.dc.im.pojo.UserInfoData;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <p>
 * 文本消息处理
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/10 16:42
 */
@Slf4j
public class CustomTextMessageHandler extends TextWebSocketHandler {

    /**
     * socket 建立成功事件
     *
     * @param session
     * @throws Exception
     */
    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        String openId = String.valueOf(session.getAttributes().get("openId"));
        UserInfoData userInfoData = (UserInfoData) session.getAttributes().get("userInfo");
        if (StringUtils.isNotBlank(openId)) {
            // 用户连接成功，放入在线用户缓存
            log.info("[连接成功] 凭证：{}, {}", openId, userInfoData);
            WsSessionManager.add(openId, session);
        } else {
            throw new RuntimeException("用户登录已经失效!");
        }
    }

    /**
     * 接收消息事件
     *
     * @param fromSession
     * @param message
     * @throws Exception
     */
    @Override
    protected void handleTextMessage(WebSocketSession fromSession, TextMessage message) throws Exception {
        // 获得客户端传来的消息
        String payload = message.getPayload();
        UserInfoData fromUser = (UserInfoData) fromSession.getAttributes().get("userInfo");
        JSONObject jsonObject = JSON.parseObject(payload);
        String toUsername = jsonObject.getString("to");
        String action = jsonObject.getString("action");
        String content = jsonObject.getString("content");
        if (StringUtils.isBlank(toUsername)) {
            return;
        }

        // 给服务的消息
        if ("server".equals(toUsername)) {
            switch (action) {
                case "getUsers":
                    // 1、推送用户列表信息
                    List<WebSocketSession> sessions = WsSessionManager.getAll();
                    List<Map<String, Object>> contents = new ArrayList<>();
                    Map<String, Object> msg = null;
                    for (WebSocketSession session : sessions) {
                        if (fromSession.equals(session)) {
                            continue;
                        }
                        msg = new HashMap<>(2);
                        UserInfoData user = (UserInfoData) session.getAttributes().get("userInfo");
                        msg.put("username", user.getUsername());
                        msg.put("avatar", user.getAvatar());
                        contents.add(msg);
                    }

                    msg = new HashMap<>(5);
                    msg.put("count", sessions.size());
                    msg.put("date", LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
                    msg.put("content", contents);
                    msg.put("from", "server");
                    msg.put("action", action);
                    fromSession.sendMessage(new TextMessage(JSON.toJSONString(msg)));
                    break;
                default:
                    ;
            }

            return;
        }

        // 给客户端的消息
        WebSocketSession toSession = WsSessionManager.getByUsername(toUsername);
        if (toSession == null) {
            /// TODO: 2021/11/11
            log.warn("[消息发送失败] 客户端未连接，用户名：{}", toUsername);
            return;
        }
        UserInfoData toUser = (UserInfoData) toSession.getAttributes().get("userInfo");
        Date sendDateTime = new Date();
        ChatContentData chatContentData = ChatContentData.builder()
                .date(sendDateTime)
                .content(content)
                .from(fromUser)
                .to(toUser)
                .self(true)
                .uid(sendDateTime.getTime())
                .build();
        fromSession.sendMessage(new TextMessage(JSON.toJSONString(chatContentData)));
        if (toSession.isOpen()) {
            toSession.sendMessage(new TextMessage(JSON.toJSONString(chatContentData.toBuilder().self(false).build())));
            recordData(chatContentData);
        }

    }

    @Async
    public void recordData(ChatContentData chatContentData) {
        log.trace("聊天数据持久化处理：{}", chatContentData);
        RecordChatContentHandler recordChatContentHandler = ApplicationContextHelper.getBean(RecordChatContentHandler.class);
        if (recordChatContentHandler != null) {
            recordChatContentHandler.handle(chatContentData);
        }
    }

    /**
     * socket 断开连接时
     *
     * @param session
     * @param status
     * @throws Exception
     */
    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        String openId = String.valueOf(session.getAttributes().get("openId"));
        UserInfoData userInfoData = (UserInfoData) session.getAttributes().get("userInfo");
        if (StringUtils.isNotBlank(openId)) {
            // 用户退出，移除缓存
            log.info("[断开连接] 凭证：{}, {}", openId, userInfoData);
            WsSessionManager.remove(openId);
            CacheData.OFFLINE_USER_INFO.put(OfflineUserInfo.builder()
                    .openId(openId)
                    .userInfoData(userInfoData)
                    .time(System.currentTimeMillis() + 2000)
                    .build());
        }
    }

}
