package com.github.dc.im.pojo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 *     上传数据
 * </p>
 *
 * @author wangpeiyuan
 * @date 2022/1/7 11:00
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
public class UploadData implements Serializable {
    private String from;
    private String to;
    private Boolean self;
    private Long uid;
}
