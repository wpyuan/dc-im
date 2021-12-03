package com.github.dc.im.listener;

import com.github.dc.im.data.CacheData;
import com.github.dc.im.manager.AuthenticateUserInfoManager;
import com.github.dc.im.pojo.OfflineUserInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * <p>
 * 离线监听处理
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/25 16:33
 */
@Component
@Slf4j
@Order(99999999)
public class OfflineListener implements CommandLineRunner {

    @Autowired
    private AuthenticateUserInfoManager authenticateUserInfoManager;

    @Override
    public void run(String... args) throws Exception {
        while (true) {
            OfflineUserInfo offlineUserInfo = CacheData.OFFLINE_USER_INFO.poll();
            if (offlineUserInfo != null) {
                authenticateUserInfoManager.remove(offlineUserInfo.getOpenId());
                log.warn("[离线] 凭证：{}, {}", offlineUserInfo.getOpenId(), offlineUserInfo.getUserInfoData());
            }
            Thread.sleep(1000);
        }
    }
}
