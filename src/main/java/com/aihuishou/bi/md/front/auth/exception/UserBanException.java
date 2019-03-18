package com.aihuishou.bi.md.front.auth.exception;

public class UserBanException extends AuthException {
    public UserBanException() {
        super(403, "账户被禁止");
    }
}
