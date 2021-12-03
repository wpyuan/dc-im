package com.github.dc.im.data;

import com.github.dc.im.pojo.OfflineUserInfo;
import com.github.dc.im.pojo.UserInfoData;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.DelayQueue;

/**
 * <p>
 *     本地缓存数据
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/24 9:55
 */
@Slf4j
public class CacheData {

    /**
     * 保存离线用户信息的地方，key：openId
     */
    public static DelayQueue<OfflineUserInfo> OFFLINE_USER_INFO = new DelayQueue<>();
}
