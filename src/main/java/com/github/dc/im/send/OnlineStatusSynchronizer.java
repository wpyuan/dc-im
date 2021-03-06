package com.github.dc.im.send;

import com.github.dc.im.constant.ConstantArgs;
import com.github.dc.im.pojo.ServerMessage;
import com.github.dc.im.pojo.UserInfoData;
import com.github.dc.im.pojo.UserStatusInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.*;

/**
 * <p>
 *     在线状态同步器，主要负责用户上线时推送该用户上线状态给其他用户
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/24 10:27
 */
@Slf4j
public class OnlineStatusSynchronizer {

    /**
     *  推送上线状态
     * @param self 上线用户会话
     */
    public static void push(WebSocketSession self) {
        UserInfoData user = (UserInfoData) self.getAttributes().get(ConstantArgs.WebSocketSession.USER_INFO);
        List<UserStatusInfo> contents = new ArrayList<>();
        contents.add(UserStatusInfo.builder()
                .username(user.getUsername())
                .avatar(user.getAvatar())
                .isOffline(false)
                .build());
        ServerMessageSender.INSTANCE.toAll(ServerMessage.<List<UserStatusInfo>>builder()
                .action(ConstantArgs.TextMessage.Payload.Action.GET_USERS)
                .content(contents)
                .date(new Date())
                .build(), self);
    }
}
