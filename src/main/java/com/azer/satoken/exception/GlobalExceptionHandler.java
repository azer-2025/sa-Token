package com.azer.satoken.exception;

import cn.dev33.satoken.exception.NotLoginException;
import cn.dev33.satoken.exception.NotPermissionException;
import cn.dev33.satoken.exception.NotRoleException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

/**
 * 全局异常处理
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 未登录异常
     */
    @ExceptionHandler(NotLoginException.class)
    public Map<String, Object> handlerNotLoginException(NotLoginException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 401);
        result.put("msg", "未登录或登录已过期");
        return result;
    }

    /**
     * 权限不足异常
     */
    @ExceptionHandler(NotPermissionException.class)
    public Map<String, Object> handlerNotPermissionException(NotPermissionException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 403);
        result.put("msg", "权限不足，无法访问：" + e.getCode());
        return result;
    }

    /**
     * 角色不足异常
     */
    @ExceptionHandler(NotRoleException.class)
    public Map<String, Object> handlerNotRoleException(NotRoleException e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 403);
        result.put("msg", "角色不符，无法访问：" + e.getCode());
        return result;
    }

    /**
     * 其他异常
     */
    @ExceptionHandler(Exception.class)
    public Map<String, Object> handlerException(Exception e) {
        Map<String, Object> result = new HashMap<>();
        result.put("code", 500);
        result.put("msg", "服务器内部错误：" + e.getMessage());
        return result;
    }
}