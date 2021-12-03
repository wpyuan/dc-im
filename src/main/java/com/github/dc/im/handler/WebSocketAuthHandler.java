package com.github.dc.im.handler;

import com.github.dc.im.pojo.UserInfoData;

import java.util.UUID;

/**
 * <p>
 *     WebSocket用户信息认证处理器，在建立连接之前的登录认证
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/24 9:44
 */
public interface WebSocketAuthHandler {

    /**
     * 用户信息认证处理
     * @param userInfoData 用户信息
     * @return 结果
     */
    Boolean isValid(UserInfoData userInfoData);

    /**
     * 返回唯一标识，默认UUID
     * @param userInfoData 用户信息
     * @return 唯一标识
     */
    default String authKey(UserInfoData userInfoData) {
        return UUID.randomUUID().toString().replaceAll("-", "");
    }
}
