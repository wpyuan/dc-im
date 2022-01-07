package com.github.dc.im.controller;

import com.github.dc.im.pojo.UploadData;
import com.github.dc.im.service.IFileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;


/**
 * <p>
 * 文件操作 Controller
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/24 9:06
 */
@RestController
@RequestMapping({"/dc-im", "/{sys}/dc-im"})
public class DcChatFileController {

    @Autowired
    private IFileService uploadService;

    @PostMapping("/upload")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> upload(@RequestParam("file") MultipartFile file, @RequestBody UploadData uploadData) {
        return ResponseEntity.ok(uploadService.upload(file, uploadData));
    }
}
