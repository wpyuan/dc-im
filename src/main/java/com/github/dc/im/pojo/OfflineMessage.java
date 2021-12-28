package com.github.dc.im.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *     离线消息
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/28 9:52
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class OfflineMessage<T> {
    /**
     * 离线消息唯一标识
     */
    private String uid;
    /**
     * 用户名
     */
    private String username;
    /**
     * 消息内容
     */
    private T message;
}
