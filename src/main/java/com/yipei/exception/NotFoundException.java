package com.yipei.exception;

/** 资源不存在异常，HTTP 404 */
public class NotFoundException extends BusinessException {
    public NotFoundException(String message) {
        super(404, message);
    }
}
