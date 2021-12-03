package com.github.dc.im.handler;

import com.github.dc.im.pojo.UserInfoData;

/**
 * <p>
 *     默认websocket认证处理，默认通过认证
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/24 9:49
 */
public class DefaultSocketAuthHandler implements WebSocketAuthHandler {

    @Override
    public Boolean isValid(UserInfoData userInfoData) {
        return true;
    }
}
