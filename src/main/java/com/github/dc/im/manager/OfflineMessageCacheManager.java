package com.github.dc.im.manager;

import com.github.dc.im.constant.ConstantArgs;
import com.github.dc.im.pojo.OfflineMessage;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <p>
 *     离线消息缓存管理
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/28 15:29
 */
@Component
@Slf4j
public class OfflineMessageCacheManager {

    private static final String IM_KEY_PREFIX = "dc:im:offline-message:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Async("dcImAsync")
    public void syncCache(String opt, OfflineMessage offlineMessage) {
        switch (opt) {
            case ConstantArgs.SyncCache.Opt.PUT:
                try {
                    stringRedisTemplate.opsForHash().putAll(IM_KEY_PREFIX + offlineMessage.getUid(), BeanUtils.describe(offlineMessage));
                } catch (Exception e) {
                    log.warn("[缓存失败] 离线消息缓存失败", e);
                }
                break;
            case ConstantArgs.SyncCache.Opt.REMOVE:
                log.debug("[离线消息缓存移除] {}", offlineMessage);
                stringRedisTemplate.delete(IM_KEY_PREFIX + offlineMessage.getUid());
                break;
            default:
                ;
        }
    }

    public List<OfflineMessage> getAll() {
        List<OfflineMessage> offlineMessageList = new ArrayList<>();
        Set<String> keys = stringRedisTemplate.keys(IM_KEY_PREFIX  + "*");
        OfflineMessage offlineMessage = null;
        HashOperations<String, String, Object> hashOperations = stringRedisTemplate.opsForHash();
        for (String key : keys) {
            Map<String, Object> map = hashOperations.entries(key);
            offlineMessage = new OfflineMessage();
            try {
                BeanUtils.populate(offlineMessage, map);
            } catch (Exception e) {
                log.warn("[获取缓存异常] 获取离线消息缓存异常", e);
            }
            offlineMessageList.add(offlineMessage);
        }

        return offlineMessageList;
    }

    public OfflineMessage get(String uid) {
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        OfflineMessage offlineMessage = new OfflineMessage();
        try {
            BeanUtils.populate(offlineMessage, hashOperations.entries(IM_KEY_PREFIX + offlineMessage.getUid()));
        } catch (Exception e) {
            log.warn("[获取缓存异常] 获取离线消息缓存异常", e);
        }
        return offlineMessage;
    }
}
