package com.github.dc.im.send;

import com.github.dc.im.pojo.ServerMessage;
import com.github.dc.im.pojo.UserInfoData;
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
        List<Map<String, Object>> contents = new ArrayList<>();
        Map<String, Object> msg = new HashMap<>(2);
        UserInfoData user = (UserInfoData) self.getAttributes().get("userInfo");
        msg.put("username", user.getUsername());
        msg.put("avatar", user.getAvatar());
        msg.put("isOffline", false);
        contents.add(msg);
        ServerMessageSender.INSTANCE.toAll(ServerMessage.<List<Map<String, Object>>>builder()
                .action("getUsers")
                .content(contents)
                .date(new Date())
                .build(), self);
    }
}