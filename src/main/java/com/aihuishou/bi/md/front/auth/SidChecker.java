package com.aihuishou.bi.md.front.auth;

import com.aihuishou.bi.md.front.auth.exception.InvalidSidException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
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
        String openId = sessionHelper.getOpenId(sid);
        if (StringUtils.isEmpty(openId)) {
            throw new InvalidSidException();
        }
        return true;
    }
}
