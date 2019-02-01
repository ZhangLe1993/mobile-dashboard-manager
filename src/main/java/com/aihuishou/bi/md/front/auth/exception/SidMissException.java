package com.aihuishou.bi.md.front.auth.exception;

public class SidMissException extends AuthException {
    public SidMissException(){
        super(4002,"need sid");
    }
}
