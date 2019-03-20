package com.aihuishou.bi.md.front.auth;

import com.aihuishou.bi.md.front.auth.exception.InvalidSidException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class SidChecker implements HandlerInterceptor {

    @Resource
    private SessionHelper sessionHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String sid = request.getHeader("sid");
        if (StringUtils.isEmpty(sid)) {
            throw new InvalidSidException();
        }
        if (StringUtils.isEmpty(sessionHelper.getOpenId(sid))) {
            throw new InvalidSidException();
        }
        return true;
    }
}
