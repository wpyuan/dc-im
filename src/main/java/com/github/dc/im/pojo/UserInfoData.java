package com.github.dc.im.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 用户信息
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/24 9:12
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UserInfoData implements Serializable {
    private String username;
    private String password;
    private String avatar;
}
