package com.yipei.exception;

/** 业务异常基类，所有业务异常统一继承此类 */
public class BusinessException extends RuntimeException {
    private final int code;

    public BusinessException(int code, String message) {
        super(message);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
