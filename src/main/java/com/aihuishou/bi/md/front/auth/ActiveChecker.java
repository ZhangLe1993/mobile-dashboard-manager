package com.aihuishou.bi.md.front.auth;

import com.aihuishou.bi.md.front.auth.exception.AuthException;
import com.aihuishou.bi.md.front.auth.exception.UserBanException;
import com.aihuishou.bi.md.front.auth.exception.UserNotActivationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@Slf4j
@Component
public class ActiveChecker implements HandlerInterceptor {
    @Resource
    private UserService userService;

    @Resource
    private SessionHelper sessionHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        User user = null;
        try {
            user = userService.findByOpenId(sessionHelper.getOpenId(request.getHeader("sid")));
        } catch (SQLException e) {
            log.error("", e);
        }
        if (user == null) {
            throw new AuthException(404,"未识别的用户");
        } else {
            if (!user.getEnable()) {
                log.warn("checkActive openId:" + user.getOpenId());
                throw new UserBanException();
            }
            if (!user.getActive()) {
                throw new UserNotActivationException();
            }
        }
        return true;
    }
}
