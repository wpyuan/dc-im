package com.github.dc.im.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.*;

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
    @JSONField(serialize = false)
    @ToString.Exclude
    private String password;
    private String avatar;
}
