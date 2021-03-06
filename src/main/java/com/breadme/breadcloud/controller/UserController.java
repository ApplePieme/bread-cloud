package com.breadme.breadcloud.controller;

import com.breadme.breadcloud.entity.User;
import com.breadme.breadcloud.entity.dto.UserDto;
import com.breadme.breadcloud.entity.vo.UserVo;
import com.breadme.breadcloud.service.UserService;
import com.breadme.breadcloud.util.R;
import com.breadme.breadcloud.util.SecurityUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 用户 控制层
 *
 * @author breadme@foxmail.com
 * @date 2022/4/27 16:20
 */
@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/register")
    public R register(@Valid @RequestBody User user) {
        userService.register(user);
        return R.success().message("注册成功, 赶快登录体验吧");
    }

    @PostMapping("/login")
    public R login(@Valid @RequestBody User user) {
        return R.success().data(userService.login(user));
    }

    @GetMapping("/logout")
    public R logout(@RequestParam("token") String token) {
        userService.logout(token);
        return R.success();
    }

    @GetMapping("/info")
    public R getUserInfo(@RequestParam("token") String token) {
        UserVo userVo = userService.getUserInfo(token);
        return R.success().data("user", userVo);
    }

    @PostMapping("/info/modified")
    public R modifiedUserInfo(@RequestBody User user) {
        userService.modifiedUserInfo(user);
        return R.success();
    }

    @PostMapping("/password/modified")
    public R modifiedPassword(@RequestBody UserDto userDto) {
        userService.modifiedPassword(userDto);
        return R.success();
    }

    @GetMapping("/rsa")
    public R getRsaKey() {
        return R.success().data("rsa", SecurityUtils.getRsaPub());
    }

    @PostMapping("/upload/avatar")
    public R uploadAvatar(@RequestParam("file") MultipartFile file) {
        return R.success().data("url", userService.uploadAvatar(file));
    }
}
