package com.azer.satoken.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.azer.satoken.entity.Role;
import com.azer.satoken.entity.User;
import com.azer.satoken.service.RoleService;
import com.azer.satoken.service.UserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户管理控制器
 */
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Resource
    private RoleService roleService;



    /**
     * 获取用户列表 (需要USER_VIEW权限)
     * @return 用户列表
     */
    @SaCheckPermission("USER_VIEW")
    @GetMapping("/list")
    public Map<String, Object> getUserList() {
        Map<String, Object> result = new HashMap<>();

        // 从数据库查询用户列表
        List<User> userList = userService.list();

        result.put("code", 200);
        result.put("msg", "获取成功");
        result.put("data", userList);

        return result;
    }

    /**
     * 分页获取用户列表 (需要USER_VIEW权限)
     * @param pageNum 当前页码
     * @param pageSize 每页条数
     * @return 用户列表
     */
    @SaCheckPermission("USER_VIEW")
    @GetMapping("/page")
    public Map<String, Object> getUserPage(@RequestParam(defaultValue = "1") Integer pageNum,
                                          @RequestParam(defaultValue = "10") Integer pageSize) {
        Map<String, Object> result = new HashMap<>();

        // 分页查询用户列表
        Page<User> page = new Page<>(pageNum, pageSize);
        Page<User> userPage = userService.page(page);

        result.put("code", 200);
        result.put("msg", "获取成功");
        Map<String, Object> data = new HashMap<>();
        data.put("records", userPage.getRecords());
        data.put("total", userPage.getTotal());
        data.put("current", userPage.getCurrent());
        data.put("size", userPage.getSize());
        result.put("data", data);

        return result;
    }

    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    @GetMapping("/info/{id}")
    public Map<String, Object> getUserInfo(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();

        User user = userService.getById(id);

        if (user != null) {
            // 获取角色名称
            Role role = roleService.getById(user.getRoleId());
            String roleName = role != null ? role.getName() : "未知角色";

            result.put("code", 200);
            result.put("msg", "获取成功");
            result.put("data", user);
            result.put("roleName", roleName); // 添加角色名称
        } else {
            result.put("code", 404);
            result.put("msg", "用户不存在");
        }

        return result;
    }

    /**
     * 添加用户 (需要USER_ADD权限)
     * @param user 用户信息
     * @return 添加结果
     */
    @SaCheckPermission("USER_ADD")
    @PostMapping("/add")
    public Map<String, Object> addUser(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();

        // 检查用户名是否已存在
        QueryWrapper<User> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", user.getUsername());
        User existingUser = userService.getOne(queryWrapper);

        if (existingUser != null) {
            result.put("code", 400);
            result.put("msg", "用户名已存在");
            return result;
        }

        // 插入用户数据
        boolean success = userService.save(user);

        if (success) {
            result.put("code", 200);
            result.put("msg", "添加成功");
            result.put("data", user);
        } else {
            result.put("code", 500);
            result.put("msg", "添加失败");
        }

        return result;
    }

    /**
     * 编辑用户 (需要USER_EDIT权限)
     * @param user 用户信息
     * @return 编辑结果
     */
    @SaCheckPermission("USER_EDIT")
    @PutMapping("/edit")
    public Map<String, Object> editUser(@RequestBody User user) {
        Map<String, Object> result = new HashMap<>();

        // 更新用户数据
        boolean success = userService.updateById(user);

        if (success) {
            result.put("code", 200);
            result.put("msg", "编辑成功");
            result.put("data", user);
        } else {
            result.put("code", 500);
            result.put("msg", "编辑失败");
        }

        return result;
    }

    /**
     * 删除用户 (需要USER_DELETE权限)
     * @param id 用户ID
     * @return 删除结果
     */
    @SaCheckPermission("USER_DELETE")
    @DeleteMapping("/delete/{id}")
    public Map<String, Object> deleteUser(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();

        // 删除用户数据
        boolean success = userService.removeById(id);

        if (success) {
            result.put("code", 200);
            result.put("msg", "删除成功");
            result.put("data", id);
        } else {
            result.put("code", 500);
            result.put("msg", "删除失败");
        }

        return result;
    }
}
