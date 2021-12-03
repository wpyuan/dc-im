package com.github.dc.im.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.concurrent.Delayed;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 * 离线用户信息
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/25 16:14
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OfflineUserInfo implements Delayed {

    /**
     * 延迟截至时间，单位ms。例：System.currentTimeMillis() + offset
     */
    private long time;
    /**
     * 用户认证标识
     */
    private String openId;
    /**
     * 用户信息
     */
    private UserInfoData userInfoData;

    @Override
    public long getDelay(TimeUnit unit) {
        return time - System.currentTimeMillis();
    }

    @Override
    public int compareTo(Delayed o) {
        OfflineUserInfo offlineUserInfo = (OfflineUserInfo) o;
        long diff = this.time - offlineUserInfo.getTime();
        if (diff <= 0) {
            return -1;
        } else {
            return 1;
        }
    }
}
