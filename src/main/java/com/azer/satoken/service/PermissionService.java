package com.azer.satoken.service;

import com.azer.satoken.entity.Permission;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.List;

public interface PermissionService extends IService<Permission> {
    
    /**
     * 根据角色ID查询权限列表
     * @param roleId 角色ID
     * @return 权限列表
     */
    List<Permission> getPermissionsByRoleId(Long roleId);
    
    /**
     * 检查角色是否拥有指定权限
     * @param roleId 角色ID
     * @param permissionName 权限名称
     * @return 是否拥有权限
     */
    boolean hasPermission(Long roleId, String permissionName);
}