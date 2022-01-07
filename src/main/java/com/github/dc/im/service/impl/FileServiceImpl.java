package com.github.dc.im.service.impl;

import com.github.dc.im.handler.FileHandler;
import com.github.dc.im.pojo.DcImApplicationContext;
import com.github.dc.im.pojo.UploadData;
import com.github.dc.im.service.IFileService;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

/**
 * <p>
 *     文件操作ServiceImpl
 * </p>
 *
 * @author wangpeiyuan
 * @date 2022/1/7 11:11
 */
@Service
public class FileServiceImpl implements IFileService {

    @Autowired
    private DcImApplicationContext dcImApplicationContext;

    @Override
    public Map<String, Object> upload(MultipartFile file, UploadData uploadData) {
        Map<String, Object> data = new HashMap<>(2);
        FileHandler fileHandler = dcImApplicationContext.getFileHandler();
        Pair<Boolean, String> result = fileHandler.upload(file, uploadData);
        data.put("success", result.getLeft());
        data.put("message", result.getRight());
        return data;
    }
}
