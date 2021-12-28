package com.github.dc.im.send;

import com.github.dc.im.constant.ConstantArgs;
import com.github.dc.im.pojo.ServerMessage;
import com.github.dc.im.pojo.UserInfoData;
import com.github.dc.im.pojo.UserStatusInfo;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <p>
 *     离线状态同步器，主要负责用户离线时推送该用户离线状态给其他用户
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/24 10:27
 */
@Slf4j
public class OfflineStatusSynchronizer {

    /**
     *  推送离线状态
     * @param userInfoData 用户
     */
    public static void push(UserInfoData userInfoData) {
        List<UserStatusInfo> contents = new ArrayList<>();
        contents.add(UserStatusInfo.builder()
                .username(userInfoData.getUsername())
                .avatar(userInfoData.getAvatar())
                .isOffline(true)
                .build());
        ServerMessageSender.INSTANCE.toAll(ServerMessage.<List<UserStatusInfo>>builder()
                .action(ConstantArgs.TextMessage.Payload.Action.GET_USERS)
                .content(contents)
                .date(new Date())
                .build());
    }
}
