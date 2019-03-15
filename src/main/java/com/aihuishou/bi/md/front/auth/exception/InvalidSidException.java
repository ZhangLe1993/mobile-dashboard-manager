package com.aihuishou.bi.md.front.auth.exception;

/**
 * 无效的会话ID，前端需要重新login以update sid
 */
public class InvalidSidException extends AuthException {
    public InvalidSidException(){
        super(401,"invalid sid");
    }
}
