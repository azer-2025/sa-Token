package com.azer.satoken.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.azer.satoken.entity.Permission;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    // 根据权限ID列表查找permission表对应的name字段
    @Select("SELECT name FROM permission WHERE id IN (#{permissionIds})")
    List<String> selectNamesByPermissionIds(List<Long> permissionIds);

}
