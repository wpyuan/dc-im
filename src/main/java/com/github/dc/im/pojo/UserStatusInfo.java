package com.github.dc.im.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *     用户状态信息
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/28 10:27
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UserStatusInfo {
    private String username;
    private String avatar;
    private Boolean isOffline;
}
