package com.azer.satoken.service.impl;

import com.azer.satoken.entity.Permission;
import com.azer.satoken.entity.RolePermission;
import com.azer.satoken.mapper.PermissionMapper;
import com.azer.satoken.mapper.RolePermissionMapper;
import com.azer.satoken.service.PermissionService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {
    
    @Autowired
    private RolePermissionMapper rolePermissionMapper;
    
    @Autowired
    private PermissionMapper permissionMapper;
    
    @Override
    public List<Permission> getPermissionsByRoleId(Long roleId) {
        // 获取角色拥有的权限ID列表
        QueryWrapper<RolePermission> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("role_id", roleId);
        List<RolePermission> rolePermissions = rolePermissionMapper.selectList(queryWrapper);
        
        // 提取权限ID
        List<Long> permissionIds = rolePermissions.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toList());
        
        // 根据权限ID列表查询权限详情
        if (!permissionIds.isEmpty()) {
            return permissionMapper.selectBatchIds(permissionIds);
        }
        
        return List.of();
    }
    
    @Override
    public boolean hasPermission(Long roleId, String permissionName) {
        // 获取角色拥有的权限列表
        List<Permission> permissions = getPermissionsByRoleId(roleId);
        
        // 检查是否包含指定权限
        return permissions.stream().anyMatch(p -> p.getName().equals(permissionName));
    }
}