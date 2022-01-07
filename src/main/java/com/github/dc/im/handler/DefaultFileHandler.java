package com.github.dc.im.handler;

import com.github.dc.im.pojo.UploadData;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.web.multipart.MultipartFile;

/**
 * <p>
 *     默认文件操作处理器，默认不处理，返回成功
 * </p>
 *
 * @author wangpeiyuan
 * @date 2022/1/7 11:19
 */
public class DefaultFileHandler implements FileHandler {

    @Override
    public Pair<Boolean, String> upload(MultipartFile file, UploadData uploadData) {
        return Pair.of(true, null);
    }
}
