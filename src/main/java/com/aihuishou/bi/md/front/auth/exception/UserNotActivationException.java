package com.aihuishou.bi.md.front.auth.exception;

public class UserNotActivationException extends AuthException {
    public UserNotActivationException(){
        super(4004,"账户需要激活");
    }
}
