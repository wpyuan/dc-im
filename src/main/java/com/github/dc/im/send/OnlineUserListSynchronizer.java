package com.github.dc.im.send;

import com.github.dc.im.constant.ConstantArgs;
import com.github.dc.im.manager.WebSocketSessionManager;
import com.github.dc.im.pojo.ServerMessage;
import com.github.dc.im.pojo.UserInfoData;
import com.github.dc.im.pojo.UserStatusInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

/**
 * <p>
 *     在线用户信息同步器，主要负责用户上线时给该用户推送在线用户列表数据
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/24 10:27
 */
@Slf4j
public class OnlineUserListSynchronizer {

    /**
     *  推送在线用户列表数据
     * @param self 上线用户会话
     */
    public static void push(WebSocketSession self) {
        List<WebSocketSession> sessions = WebSocketSessionManager.getAll();
        List<UserStatusInfo> contents = new ArrayList<>();
        for (WebSocketSession session : sessions) {
            if (self.equals(session)) {
                continue;
            }
            UserInfoData user = (UserInfoData) session.getAttributes().get(ConstantArgs.WebSocketSession.USER_INFO);
            contents.add(UserStatusInfo.builder()
                    .username(user.getUsername())
                    .avatar(user.getAvatar())
                    .isOffline(false)
                    .build());
        }

        ServerMessageSender.INSTANCE.to(ServerMessage.<List<UserStatusInfo>>builder()
                .action(ConstantArgs.TextMessage.Payload.Action.GET_USERS)
                .content(contents)
                .date(new Date())
                .build(), self);
    }
}
