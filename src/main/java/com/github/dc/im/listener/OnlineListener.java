package com.github.dc.im.listener;

import com.github.dc.im.manager.AuthenticateUserInfoManager;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

/**
 * <p>
 *     在线监听缓存初始化
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/29 17:11
 */
@Component
@Slf4j
@Order(1)
public class OnlineListener implements CommandLineRunner {

    @Autowired
    private AuthenticateUserInfoManager authenticateUserInfoManager;

    @Override
    public void run(String... args) throws Exception {
        authenticateUserInfoManager.init();
        log.debug("[ws凭证缓存] 缓存成功");
    }
}
