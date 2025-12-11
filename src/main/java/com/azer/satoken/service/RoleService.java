package com.azer.satoken.service;

import com.azer.satoken.entity.Permission;
import com.azer.satoken.entity.Role;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @PROJECT_NAME saToken
 * @Author Azer
 * @DESCRIPTION
 * @Date 2025-12-08 14:53
 */
public interface RoleService extends IService<Role> {
    /**
     * 根据ID获取角色
     * @param id 角色ID
     * @return 角色对象
     */
    Role getById(Long id);
}
