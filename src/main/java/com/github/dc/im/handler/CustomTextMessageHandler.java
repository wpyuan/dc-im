package com.github.dc.im.handler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.github.dc.im.constant.ConstantArgs;
import com.github.dc.im.data.CacheData;
import com.github.dc.im.manager.WebSocketSessionManager;
import com.github.dc.im.pojo.Content;
import com.github.dc.im.pojo.OfflineUserInfo;
import com.github.dc.im.pojo.UserInfoData;
import com.github.dc.im.send.ChatMessageSender;
import com.github.dc.im.send.OfflineMessageSynchronizer;
import com.github.dc.im.send.OnlineStatusSynchronizer;
import com.github.dc.im.send.OnlineUserListSynchronizer;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

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
        String openId = String.valueOf(session.getAttributes().get(ConstantArgs.WebSocketSession.KEY));
        UserInfoData userInfoData = (UserInfoData) session.getAttributes().get(ConstantArgs.WebSocketSession.USER_INFO);
        if (StringUtils.isNotBlank(openId)) {
            // 用户连接成功，放入在线用户缓存
            log.info("[连接成功] 凭证：{}, {}", openId, userInfoData);
            WebSocketSessionManager.add(openId, session);
            // 推送上线状态
            OnlineStatusSynchronizer.push(session);
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
        UserInfoData fromUser = (UserInfoData) fromSession.getAttributes().get(ConstantArgs.WebSocketSession.USER_INFO);
        JSONObject jsonObject = JSON.parseObject(payload);
        String toUsername = jsonObject.getString(ConstantArgs.TextMessage.Payload.TO);
        String action = jsonObject.getString(ConstantArgs.TextMessage.Payload.ACTION);
        String contentJSON = jsonObject.getString(ConstantArgs.TextMessage.Payload.CONTENT);
        Content content = JSON.parseObject(contentJSON, Content.class);
        if (StringUtils.isBlank(toUsername)) {
            return;
        }

        // 给服务的消息
        if (ConstantArgs.TextMessage.Payload.To.SERVER.equals(toUsername)) {
            switch (action) {
                case ConstantArgs.TextMessage.Payload.Action.GET_USERS:
                    // 1、推送用户列表信息
                    OnlineUserListSynchronizer.push(fromSession);
                    // 2、推送离线消息
                    OfflineMessageSynchronizer.push(fromSession);
                    break;
                default:
                    ;
            }

            return;
        }

        // 给客户端的消息
        ChatMessageSender.INSTANCE.to(content, fromSession, toUsername);
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
        String openId = String.valueOf(session.getAttributes().get(ConstantArgs.WebSocketSession.KEY));
        UserInfoData userInfoData = (UserInfoData) session.getAttributes().get(ConstantArgs.WebSocketSession.USER_INFO);
        if (StringUtils.isNotBlank(openId)) {
            // 用户退出，移除缓存
            log.info("[断开连接] 凭证：{}, {}", openId, userInfoData);
            WebSocketSessionManager.remove(openId);
            CacheData.OFFLINE_USER_INFO.put(OfflineUserInfo.builder()
                    .openId(openId)
                    .userInfoData(userInfoData)
                    .time(System.currentTimeMillis() + 2000)
                    .build());
        }
    }
}
