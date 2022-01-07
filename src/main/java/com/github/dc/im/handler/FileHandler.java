package com.github.dc.im.handler;

import com.github.dc.im.pojo.UploadData;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 * 文件操作处理器
 * </p>
 *
 * @author wangpeiyuan
 * @date 2022/1/7 11:15
 */
public interface FileHandler {

    /**
     * 上传文件处理
     * @param file 文件
     * @param uploadData 文件数据
     * @return 处理结果，left：是否成功；right：错误信息
     */
    Pair<Boolean, String> upload(MultipartFile file, UploadData uploadData);
}
