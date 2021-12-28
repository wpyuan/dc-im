package com.github.dc.im.manager;

import com.github.dc.im.constant.ConstantArgs;
import com.github.dc.im.data.CacheData;
import com.github.dc.im.helper.ApplicationContextHelper;
import com.github.dc.im.pojo.OfflineUserInfo;
import com.github.dc.im.pojo.UserInfoData;
import com.github.dc.im.send.OfflineStatusSynchronizer;
import com.github.dc.im.util.EntityUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.ConvertUtils;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.lang.reflect.Field;
import java.text.ParseException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * <p>
 * 已认证用户信息管理
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/26 10:51
 */
@Component
@Slf4j
public class AuthenticateUserInfoManager {

    /**
     * 保存认证通过在线用户信息的地方，key：openId
     */
    private ConcurrentHashMap<String, UserInfoData> AUTHENTICATE_USER_INFO = new ConcurrentHashMap<>();
    private static final String IM_KEY_PREFIX = "dc:im:";

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    public void init() {
        Set<String> cacheKeys = stringRedisTemplate.keys(IM_KEY_PREFIX + "**");
        if (CollectionUtils.isEmpty(cacheKeys)) {
            return;
        }
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        Map<String, String> data = null;
        for (String cacheKey : cacheKeys) {
            data = hashOperations.entries(cacheKey);
            put(cacheKey.substring(8), mapToBean(data, UserInfoData.class), true);
        }
    }

    public void put(String key, UserInfoData userInfoData) {
        // 删除该用户的其他凭证信息
        removeByUsername(userInfoData.getUsername());
        put(key, userInfoData, true);
    }

    public void put(String key, UserInfoData userInfoData, boolean syncCache) {
        AUTHENTICATE_USER_INFO.put(key, userInfoData);
        if (syncCache) {
            _this().syncCache(ConstantArgs.SyncCache.Opt.PUT, key, userInfoData);
        }
    }

    public void remove(String key) {
        AUTHENTICATE_USER_INFO.remove(key);
        _this().syncCache(ConstantArgs.SyncCache.Opt.REMOVE, key, null);
    }

    public void remove(Set<String> keys) {
        if (CollectionUtils.isEmpty(keys)) {
            return;
        }
        keys.forEach(this::remove);
    }

    public void removeByUsername(String username) {
        this.remove(this.getKeys(username));
    }

    public boolean containsKey(String key) {
        return AUTHENTICATE_USER_INFO.containsKey(key) || stringRedisTemplate.hasKey(IM_KEY_PREFIX + key);
    }

    public UserInfoData get(String key) {
        if (!containsKey(key)) {
            return null;
        }
        if (AUTHENTICATE_USER_INFO.containsKey(key)) {
            return AUTHENTICATE_USER_INFO.get(key);
        }
        if (stringRedisTemplate.hasKey(IM_KEY_PREFIX + key)) {
            HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
            UserInfoData userInfoData = mapToBean(hashOperations.entries(IM_KEY_PREFIX + key), UserInfoData.class);
            _this().syncLocal(ConstantArgs.SyncCache.Opt.PUT, key, userInfoData);
            return userInfoData;
        }
        return null;
    }

    /**
     * 根据用户名获取认证通过在线用户的认证标识
     *
     * @param username 用户名
     * @return 认证标识
     */
    public String getKey(String username) {
        Objects.requireNonNull(username, "用户名不能为空");
        return AUTHENTICATE_USER_INFO.entrySet().stream().filter(u -> username.equals(u.getValue().getUsername())).findFirst().get().getKey();
    }

    /**
     * 根据用户名获取认证通过在线用户的认证标识
     *
     * @param username 用户名
     * @return 认证标识
     */
    public Set<String> getKeys(String username) {
        Objects.requireNonNull(username, "用户名不能为空");
        Set<String> keys = AUTHENTICATE_USER_INFO.entrySet().stream().filter(u -> username.equals(u.getValue().getUsername())).map(u -> u.getKey()).collect(Collectors.toSet());
        // 缓存的
        Set<String> cacheKeys = stringRedisTemplate.keys(IM_KEY_PREFIX + "**");
        if (CollectionUtils.isEmpty(cacheKeys)) {
            return keys;
        }
        HashOperations<String, String, String> hashOperations = stringRedisTemplate.opsForHash();
        Map<String, String> data = null;
        for (String cacheKey : cacheKeys) {
            data = hashOperations.entries(cacheKey);
            if (username.equals(data.get("username"))) {
                keys.add(cacheKey.substring(8));
            }
        }
        return keys;
    }

    public Map<String, UserInfoData> getAll() {
        return MapUtils.unmodifiableMap(AUTHENTICATE_USER_INFO);
    }

    @Async("dcImAsync")
    public void syncLocal(String opt, String key, UserInfoData userInfoData) {
        switch (opt) {
            case ConstantArgs.SyncCache.Opt.PUT:
                put(key, userInfoData, false);
                break;
            default:
                ;
        }
    }

    @Async("dcImAsync")
    public void syncCache(String opt, String key, UserInfoData userInfoData) {
        switch (opt) {
            case ConstantArgs.SyncCache.Opt.PUT:
                stringRedisTemplate.opsForHash().putAll(IM_KEY_PREFIX + key, beanToMap(userInfoData));
                break;
            case ConstantArgs.SyncCache.Opt.REMOVE:
                log.debug("[凭证缓存移除] {}", key);
                stringRedisTemplate.delete(IM_KEY_PREFIX + key);
                break;
            default:
                ;
        }
    }

    @Scheduled(cron = "0/1 * * * * ?")
    public void handleOffline() throws IOException {
        OfflineUserInfo offlineUserInfo = CacheData.OFFLINE_USER_INFO.poll();
        if (offlineUserInfo != null) {
            remove(offlineUserInfo.getOpenId());
            // 推送离线状态，刷新的除外
            if (WebSocketSessionManager.isOnline(offlineUserInfo.getUserInfoData().getUsername())) {
                return;
            }
            UserInfoData user = offlineUserInfo.getUserInfoData();
            OfflineStatusSynchronizer.push(user);
            log.warn("[离线] 凭证：{}, 用户：{}", offlineUserInfo.getOpenId(), offlineUserInfo.getUserInfoData());
        }
    }

    private AuthenticateUserInfoManager _this() {
       return ApplicationContextHelper.getBean(AuthenticateUserInfoManager.class);
    }

    private <B> Map<String, String> beanToMap(B bean) {
        Map<String, String> map = new HashMap<>(1);
        Class clazz = bean.getClass();
        Field[] field = clazz.getDeclaredFields();
        for (Field f : field) {
            f.setAccessible(true);
            try {
                if (f.get(bean) == null || "password".equals(f.getName())) {
                    continue;
                } else if (Date.class.equals(f.getType())) {
                    map.put(f.getName(), DateFormatUtils.format((Date) f.get(bean), "yyyy-MM-dd HH:mm:ss"));
                } else {
                    map.put(f.getName(), String.valueOf(f.get(bean)));
                }
            } catch (IllegalAccessException e) {
                log.warn("'beanToMap' Function Running ERROR", e);
            }
        }
        return map;
    }

    private <B> B mapToBean(Map<String, String> map, Class<B> bClass) {
        if (MapUtils.isEmpty(map)) {
            return null;
        }
        B b = EntityUtil.instance(bClass);
        Field[] field = bClass.getDeclaredFields();
        for (Field f : field) {
            f.setAccessible(true);
            try {
                if (map.get(f.getName()) == null) {
                    continue;
                } else if (Date.class.equals(f.getType())) {
                    f.set(b, DateUtils.parseDate(map.get(f.getName()), "yyyy-MM-dd HH:mm:ss"));
                } else {
                    f.set(b, ConvertUtils.convert(map.get(f.getName()), f.getType()));
                }
            } catch (IllegalAccessException | ParseException e) {
                log.warn("'mapToBean' Function Running ERROR", e);
            }

        }
        return b;
    }
}
