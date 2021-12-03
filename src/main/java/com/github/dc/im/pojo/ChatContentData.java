package com.github.dc.im.pojo;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 聊天内容
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/26 9:16
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class ChatContentData implements Serializable {
    private UserInfoData from;
    private UserInfoData to;
    @JSONField(format = "yyyy-MM-dd HH:mm:ss")
    private Date date;
    private String content;
    private Boolean self;
    private Long uid;
}
