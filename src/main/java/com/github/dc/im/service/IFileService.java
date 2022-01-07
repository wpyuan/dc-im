package com.github.dc.im.service;

import com.github.dc.im.pojo.UploadData;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * <p>
 *     文件操作Service
 * </p>
 *
 * @author wangpeiyuan
 * @date 2022/1/7 11:10
 */
public interface IFileService {
    /**
     * 上传
     * @param file 文件
     * @param uploadData 上传附带数据
     * @return 是否上传成功等结果信息
     */
    Map<String, Object> upload(MultipartFile file, UploadData uploadData);
}
