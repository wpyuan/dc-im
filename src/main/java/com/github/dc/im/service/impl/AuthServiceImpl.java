package com.github.dc.im.service.impl;

import com.github.dc.im.config.DcImProperties;
import com.github.dc.im.constant.ConstantArgs;
import com.github.dc.im.handler.WebSocketAuthHandler;
import com.github.dc.im.manager.AuthenticateUserInfoManager;
import com.github.dc.im.pojo.DcImApplicationContext;
import com.github.dc.im.pojo.UserInfoData;
import com.github.dc.im.service.IAuthService;
import com.github.dc.im.util.RSAUtil;
import org.apache.commons.lang3.StringUtils;
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
    private DcImProperties dcImProperties;
    @Autowired
    private AuthenticateUserInfoManager authenticateUserInfoManager;
    @Autowired
    private DcImApplicationContext dcImApplicationContext;

    @Override
    public Map<String, Object> authorize(UserInfoData userInfoData) {
        Map<String, Object> data = new HashMap<>(2);
        if (userInfoData == null || StringUtils.isBlank(userInfoData.getUsername())) {
            data.put("success", false);
            data.put("message", "用户名不能为空！");
            return data;
        }

        data.put("success", true);
        data.put("data", dcImProperties.getAuthorize().getPublicKey());
        return data;
    }

    @Override
    public Map<String, Object> login(UserInfoData userInfoData) {
        // 解密
        userInfoData.setPassword(RSAUtil.decryptByPrivateKey(userInfoData.getPassword(), dcImProperties.getAuthorize().getPrivateKey()));
        Map<String, Object> data = new HashMap<>(2);
        WebSocketAuthHandler webSocketAuthHandler = dcImApplicationContext.getWebSocketAuthHandler();
        if (!webSocketAuthHandler.isValid(userInfoData)) {
            data.put("success", false);
            return data;
        }
        String authKey = webSocketAuthHandler.authKey(userInfoData);
        authenticateUserInfoManager.put(authKey, userInfoData);
        data.put("success", true);
        data.put(ConstantArgs.WebSocketSession.KEY, authKey);
        data.put(ConstantArgs.WebSocketSession.USER_INFO, userInfoData);
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
        WebSocketAuthHandler webSocketAuthHandler = dcImApplicationContext.getWebSocketAuthHandler();
        String authKey = webSocketAuthHandler.authKey(userInfoData);
        authenticateUserInfoManager.put(authKey, userInfoData);
        data.put("success", true);
        data.put(ConstantArgs.WebSocketSession.KEY, authKey);
        return data;
    }
}
