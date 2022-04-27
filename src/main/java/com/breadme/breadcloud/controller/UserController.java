package com.breadme.breadcloud.controller;

import com.breadme.breadcloud.entity.User;
import com.breadme.breadcloud.entity.vo.UserVo;
import com.breadme.breadcloud.service.UserService;
import com.breadme.breadcloud.util.R;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;

/**
 * 用户 控制层
 *
 * @author breadme@foxmail.com
 * @date 2022/4/27 16:20
 */
@RestController
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

    @GetMapping("/user/info")
    public R getUserInfo(@RequestParam("token") String token) {
        UserVo userVo = userService.getUserInfo(token);
        return R.success().data("user", userVo);
    }
}
