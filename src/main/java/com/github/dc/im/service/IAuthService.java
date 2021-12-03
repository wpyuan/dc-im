package com.github.dc.im.service;

import com.github.dc.im.pojo.UserInfoData;

import java.util.Map;

/**
 * <p>
 *     登录 Service
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/24 11:13
 */
public interface IAuthService {
    /**
     * 登录认证处理
     * @param userInfoData 用户信息
     * @return 认证结果
     */
    Map<String, Object> login(UserInfoData userInfoData);

    /**
     * 刷新认证处理
     * @param openId 用户认证
     * @return 处理结果
     */
    Map<String, Object> refresh(String openId);
}
