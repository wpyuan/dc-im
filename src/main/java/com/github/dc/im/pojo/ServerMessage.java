package com.github.dc.im.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import com.github.dc.im.enums.SendReliability;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *     服务端消息
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/12/24 9:54
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ServerMessage<T> implements Serializable {
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;
    private T content;
    @Builder.Default
    private String from = "server";
    private String action;
    @Builder.Default
    @JSONField(serialize = false)
    private SendReliability sendReliability = SendReliability.ONCE;
}
