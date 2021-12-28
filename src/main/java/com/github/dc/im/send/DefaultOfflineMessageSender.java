package com.github.dc.im.send;

import com.github.dc.im.constant.ConstantArgs;
import com.github.dc.im.data.CacheData;
import com.github.dc.im.helper.ApplicationContextHelper;
import com.github.dc.im.manager.OfflineMessageCacheManager;
import com.github.dc.im.pojo.OfflineMessage;
import com.github.dc.im.pojo.UserInfoData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.UUID;

/**
 * <p>
 * 默认离线消息发送器。放入本地缓存同步远程缓存，等上线后推送离线消息
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/28 8:17
 */
@Slf4j
public class DefaultOfflineMessageSender<T> implements OfflineMessageSender<T> {

    @Override
    public void handlerSend(T message, WebSocketSession session) {
        if (!session.getAttributes().containsKey(ConstantArgs.WebSocketSession.USER_INFO)) {
            log.warn("[发送失败] 发送离线消息失败，用户信息为空，消息作废处理！消息：{}", message);
        }
        UserInfoData userInfoData = (UserInfoData) session.getAttributes().get(ConstantArgs.WebSocketSession.USER_INFO);
        OfflineMessage offlineMessage = OfflineMessage.builder()
                .uid(UUID.randomUUID().toString())
                .message(message)
                .username(userInfoData.getUsername())
                .build();
        // 本地缓存
        CacheData.OFFLINE_MESSAGE.add(offlineMessage);
        // 同步备份远程缓存
        OfflineMessageCacheManager offlineMessageCacheManager = ApplicationContextHelper.getBean(OfflineMessageCacheManager.class);
        offlineMessageCacheManager.syncCache(ConstantArgs.SyncCache.Opt.PUT, offlineMessage);
    }
}
