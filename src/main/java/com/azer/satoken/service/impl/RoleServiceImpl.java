package com.azer.satoken.service.impl;

import com.azer.satoken.entity.Permission;
import com.azer.satoken.entity.Role;
import com.azer.satoken.mapper.PermissionMapper;
import com.azer.satoken.mapper.RoleMapper;
import com.azer.satoken.service.PermissionService;
import com.azer.satoken.service.RoleService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;

/**
 * @PROJECT_NAME saToken
 * @Author Azer
 * @DESCRIPTION
 * @Date 2025-12-08 14:53
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Override
    public Role getById(Long id) {
        return this.baseMapper.selectById(id);
    }
}
