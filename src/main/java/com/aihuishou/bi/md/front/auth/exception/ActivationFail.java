package com.aihuishou.bi.md.front.auth.exception;

public class ActivationFail extends AuthException {
    public ActivationFail() {
        super(400, "账户激活失败");
    }
}
