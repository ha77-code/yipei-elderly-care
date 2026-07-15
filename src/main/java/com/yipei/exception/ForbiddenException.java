package com.yipei.exception;

/** 权限不足异常，HTTP 403 */
public class ForbiddenException extends BusinessException {
    public ForbiddenException(String message) {
        super(403, message);
    }
}
