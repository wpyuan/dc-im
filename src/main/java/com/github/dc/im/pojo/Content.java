package com.github.dc.im.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * <p>
 *     内容
 * </p>
 *
 * @author wangpeiyuan
 * @date 2022/1/7 15:36
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class Content {
    /**
     * 唯一标识，允许为空，上传文件时不为空
     */
    private String uid;
    /**
     * 类型
     */
    private String type;
    /**
     * 文本内容
     */
    private String text;
    /**
     * 发送是否成功
     */
    @Builder.Default
    private Boolean success = true;
}
