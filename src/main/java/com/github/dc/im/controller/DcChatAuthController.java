package com.github.dc.im.controller;

import com.github.dc.im.pojo.UserInfoData;
import com.github.dc.im.service.IAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;


/**
 * <p>
 *     认证 Controller
 * </p>
 *
 * @author wangpeiyuan
 * @date 2021/11/24 9:06
 */
@RestController
@RequestMapping({"/dc-im", "/{sys}/dc-im"})
public class DcChatAuthController {

    @Autowired
    private IAuthService authService;

    @PostMapping("/authorize")
    public ResponseEntity<Map<String, Object>> authorize(@RequestBody UserInfoData userInfoData) {
        return ResponseEntity.ok(authService.authorize(userInfoData));
    }

    @PostMapping("/authorize/token")
    public ResponseEntity<Map<String, Object>> token(@RequestBody UserInfoData userInfoData) {
        return ResponseEntity.ok(authService.login(userInfoData));
    }

    @PostMapping("/refresh")
    public ResponseEntity<Map<String, Object>> refresh(@RequestBody String openId) {
        return ResponseEntity.ok(authService.refresh(openId));
    }
}
