package com.github.dc.im.service.impl;

import com.github.dc.im.handler.DefaultSocketAuthHandler;
import com.github.dc.im.helper.ApplicationContextHelper;
import com.github.dc.im.manager.AuthenticateUserInfoManager;
import com.github.dc.im.pojo.UserInfoData;
import com.github.dc.im.service.IAuthService;
import com.github.dc.im.handler.WebSocketAuthHandler;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *     登录 ServiceImpl
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/24 11:15
 */
@Service
public class AuthServiceImpl implements IAuthService {

    @Autowired
    private AuthenticateUserInfoManager authenticateUserInfoManager;

    @Override
    public Map<String, Object> login(UserInfoData userInfoData) {
        Map<String, Object> data = new HashMap<>(2);
        WebSocketAuthHandler webSocketAuthHandler = ObjectUtils.defaultIfNull(ApplicationContextHelper.getBean(WebSocketAuthHandler.class), new DefaultSocketAuthHandler());
        if (!webSocketAuthHandler.isValid(userInfoData)) {
            data.put("success", false);
            return data;
        }
        String authKey = webSocketAuthHandler.authKey(userInfoData);
        authenticateUserInfoManager.put(authKey, userInfoData);
        data.put("success", true);
        data.put("openId", authKey);
        return data;
    }

    @Override
    public Map<String, Object> refresh(String openId) {
        Map<String, Object> data = new HashMap<>(2);
        if (!authenticateUserInfoManager.containsKey(openId)) {
            data.put("success", false);
            return data;
        }
        UserInfoData userInfoData = authenticateUserInfoManager.get(openId);
        WebSocketAuthHandler webSocketAuthHandler = ObjectUtils.defaultIfNull(ApplicationContextHelper.getBean(WebSocketAuthHandler.class), new DefaultSocketAuthHandler());
        String authKey = webSocketAuthHandler.authKey(userInfoData);
        authenticateUserInfoManager.put(authKey, userInfoData);
        data.put("success", true);
        data.put("openId", authKey);
        return data;
    }
}
