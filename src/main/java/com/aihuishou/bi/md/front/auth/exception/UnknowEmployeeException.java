package com.aihuishou.bi.md.front.auth.exception;

public class UnknowEmployeeException extends AuthException {
    public UnknowEmployeeException(){
        super(4003,"未登记的公司职员");
    }
}
