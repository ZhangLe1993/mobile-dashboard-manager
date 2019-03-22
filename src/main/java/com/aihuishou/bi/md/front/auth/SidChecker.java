package com.aihuishou.bi.md.front.auth;

import com.aihuishou.bi.md.front.auth.exception.InvalidSidException;
import com.aihuishou.bi.md.front.auth.exception.UserBanException;
import com.aihuishou.bi.md.front.auth.exception.UserNotActivationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

@Slf4j
@Component
public class SidChecker implements HandlerInterceptor {

    @Resource
    private UserService userService;

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
        } else {
            User user = null;
            try {
                user = userService.findByOpenId(openId);
            } catch (SQLException e) {
                log.error("", e);
            }
            if (user == null) {
                throw new InvalidSidException();
            } else {
                if (!user.getEnable()) {
                    throw new UserBanException();
                }
                if (!user.getActive()) {
                    throw new UserNotActivationException();
                }
            }
        }
        return true;
    }
}
