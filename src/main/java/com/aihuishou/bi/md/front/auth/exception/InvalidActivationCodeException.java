package com.aihuishou.bi.md.front.auth.exception;

public class InvalidActivationCodeException extends AuthException {
    public InvalidActivationCodeException() {
        super(4005, "无效的激活码");
    }
}
