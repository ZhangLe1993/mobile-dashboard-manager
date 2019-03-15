package com.aihuishou.bi.md.front.auth.exception;

public class WeixinAuthFailException extends AuthException  {

    public WeixinAuthFailException() {
        super(500, "微信Server校验失败");
    }
}
