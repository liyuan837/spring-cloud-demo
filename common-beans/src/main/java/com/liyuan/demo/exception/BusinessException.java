package com.liyuan.demo.exception;

public class BusinessException extends RuntimeException {

    private static final long serialVersionUID = -6202759931628287230L;
    private static final int DEFAULT_ERROR_CODE = 201;

    /* 错误码,用于返回接口code */
    private int errCode;

    public BusinessException() {
        super();
    }

    public BusinessException(String msg) {
        super(msg);
        this.errCode = DEFAULT_ERROR_CODE;
    }

    public BusinessException(int errCode, String msg) {
        super(msg);
        this.errCode = errCode;
    }

    public BusinessException(String msg, Throwable e) {
        super(msg, e);
        this.errCode = DEFAULT_ERROR_CODE;
    }

    public BusinessException(int errCode, String msg, Throwable e) {
        super(msg, e);
        this.errCode = errCode;
    }

    public int getErrCode() {
        return errCode;
    }
}