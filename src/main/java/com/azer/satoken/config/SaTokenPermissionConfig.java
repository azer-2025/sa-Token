package com.azer.satoken.config;

import cn.dev33.satoken.stp.StpInterface;
import com.azer.satoken.entity.Permission;
import com.azer.satoken.entity.Role;
import com.azer.satoken.entity.User;
import com.azer.satoken.service.PermissionService;
import com.azer.satoken.service.RoleService;
import com.azer.satoken.service.UserService;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 自定义权限验证接口扩展
 */
@Component
public class SaTokenPermissionConfig implements StpInterface {

    @Autowired
    private UserService userService;

    @Autowired
    private PermissionService permissionService;

    @Resource
    private RoleService roleService;

    /**
     * 返回一个用户所拥有的权限集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        // 获取用户信息
        User user = userService.getById(Long.valueOf(loginId.toString()));

        // 获取用户角色对应的权限列表
        List<Permission> permissions = permissionService.getPermissionsByRoleId(user.getRoleId());

        // 提取权限名称
        List<String> permissionNames = new ArrayList<>();
        for (Permission permission : permissions) {
            permissionNames.add(permission.getName());
        }

        return permissionNames;
    }

    /**
     * 返回一个用户所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        // 获取用户信息
        User user = userService.getById(Long.valueOf(loginId.toString()));

        // 获取角色信息
        Role role = roleService.getById(user.getRoleId());

        // 将角色名称作为角色标识返回
        List<String> roleNames = new ArrayList<>();
        roleNames.add(role.getName());

        return roleNames;
    }
}
