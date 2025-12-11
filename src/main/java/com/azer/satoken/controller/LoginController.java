package com.azer.satoken.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.azer.satoken.entity.User;
import com.azer.satoken.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 认证控制器
 */
@RestController
@RequestMapping("/auth")
public class LoginController {

    @Autowired
    private UserService userService;

    /**
     * 用户登录接口
     * @param loginUser 包含用户名和密码的用户对象
     * @return 登录结果
     */
    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody User loginUser) {
        Map<String, Object> result = new HashMap<>();

        // 用户认证
        User user = userService.login(loginUser.getUsername(), loginUser.getPassword());
        if (user != null) {
            // Sa-Token 登录认证
            StpUtil.login(user.getId());

            result.put("code", 200);
            result.put("msg", "登录成功");
            Map<String, Object> data = new HashMap<>();
            data.put("satoken", StpUtil.getTokenValue());
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("roleId", user.getRoleId());
            result.put("data", data);
        } else {
            result.put("code", 401);
            result.put("msg", "用户名或密码错误");
        }

        return result;
    }

    /**
     * 用户退出登录接口
     * @return 退出结果
     */
    @PostMapping("/logout")
    public Map<String, Object> logout() {
        Map<String, Object> result = new HashMap<>();

        // Sa-Token 退出登录
        StpUtil.logout();

        result.put("code", 200);
        result.put("msg", "退出成功");

        return result;
    }

    /**
     * 获取当前登录用户信息
     * @return 用户信息
     */
    @GetMapping("/info")
    public Map<String, Object> userInfo() {
        Map<String, Object> result = new HashMap<>();

        if (StpUtil.isLogin()) {
            Long userId = StpUtil.getLoginIdAsLong();
            User user = userService.getById(userId);

            result.put("code", 200);
            result.put("msg", "获取成功");
            Map<String, Object> data = new HashMap<>();
            data.put("userId", user.getId());
            data.put("username", user.getUsername());
            data.put("roleId", user.getRoleId());
            result.put("data", data);
        } else {
            result.put("code", 401);
            result.put("msg", "未登录");
        }

        return result;
    }
}
