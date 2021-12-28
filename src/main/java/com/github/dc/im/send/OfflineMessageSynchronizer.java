package com.github.dc.im.send;

import com.github.dc.im.constant.ConstantArgs;
import com.github.dc.im.data.CacheData;
import com.github.dc.im.helper.ApplicationContextHelper;
import com.github.dc.im.manager.OfflineMessageCacheManager;
import com.github.dc.im.pojo.ChatContentData;
import com.github.dc.im.pojo.OfflineMessage;
import com.github.dc.im.pojo.ServerMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.socket.WebSocketSession;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * <p>
 *     离线消息同步器，主要负责用户上线时给该用户推送离线消息
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/24 10:27
 */
@Slf4j
public class OfflineMessageSynchronizer {

    /**
     *  推送离线消息
     * @param self 上线用户会话
     */
    public static void push(WebSocketSession self) {
        // 用户离线消息
        OfflineMessageCacheManager offlineMessageCacheManager = ApplicationContextHelper.getBean(OfflineMessageCacheManager.class);
        List<OfflineMessage> offlineMessageList = offlineMessageCacheManager.getAll();
        CacheData.OFFLINE_MESSAGE.addAll(offlineMessageList);
        CacheData.OFFLINE_MESSAGE = CacheData.OFFLINE_MESSAGE.stream().collect(Collectors.collectingAndThen(Collectors.toCollection(() -> new TreeSet<>(Comparator.comparing(OfflineMessage::getUid))), CopyOnWriteArrayList::new));
        Map<String, List<OfflineMessage>> userOfflineMessage = CacheData.OFFLINE_MESSAGE.stream().collect(Collectors.groupingBy(group->group.getUsername()));
        userOfflineMessage.forEach((username, list) -> {
            for (OfflineMessage offlineMessage : list) {
                if (offlineMessage.getMessage() instanceof ServerMessage) {
                    ServerMessageSender.INSTANCE.to((ServerMessage) offlineMessage.getMessage(), self);
                } else if (offlineMessage.getMessage() instanceof ChatContentData) {
                    ChatMessageSender.INSTANCE.handlerSend((ChatContentData) offlineMessage.getMessage(), self);
                }
                // 移除缓存
                CacheData.OFFLINE_MESSAGE.remove(offlineMessage);
                offlineMessageCacheManager.syncCache(ConstantArgs.SyncCache.Opt.REMOVE, offlineMessage);
            }
        });
    }
}
