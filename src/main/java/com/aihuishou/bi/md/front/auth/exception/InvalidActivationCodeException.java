package com.aihuishou.bi.md.front.auth.exception;

public class InvalidActivationCodeException extends AuthException {
    public InvalidActivationCodeException() {
        super(400, "无效的激活码");
    }
}
