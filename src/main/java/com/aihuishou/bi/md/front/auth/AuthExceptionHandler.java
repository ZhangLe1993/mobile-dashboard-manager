package com.aihuishou.bi.md.front.auth;

import com.aihuishou.bi.md.front.auth.exception.AuthException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@ControllerAdvice
@ResponseBody
public class AuthExceptionHandler {

    /**
     * Auth Exception
     */
    @ExceptionHandler(AuthException.class)
    public void handleHttpMessageNotReadableException(AuthException e, HttpServletResponse response) {
        try {
            log.warn(e.getMsg());
            response.setStatus(e.getCode());
            response.setCharacterEncoding("utf-8");
            response.getWriter().println(e.getMsg());
            response.setContentType("text/plain;charset=utf-8");
            response.flushBuffer();
        } catch (IOException e1) {
            log.error("",e1);
        }
    }
}
