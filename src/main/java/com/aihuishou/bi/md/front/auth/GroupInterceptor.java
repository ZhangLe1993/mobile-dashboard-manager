package com.aihuishou.bi.md.front.auth;

import com.aihuishou.bi.md.front.auth.exception.AuthException;
import com.aihuishou.bi.md.front.auth.exception.UserBanException;
import com.aihuishou.bi.md.front.auth.exception.UserNotActivationException;
import com.aihuishou.bi.md.front.chart.enums.ServiceValue;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.List;

@Slf4j
@Component
public class GroupInterceptor implements HandlerInterceptor {

    @Resource
    private GroupService groupService;

    @Resource
    private UserService userService;

    @Resource
    private SessionHelper sessionHelper;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
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
        String service = request.getParameter("service_type");
        List<String> groups = groupService.list(user.getOpenId());

        if(groups == null || groups.size() == 0) {
            throw new UserBanException();
        }

        if(groups.contains(ServiceValue.CTB.getKey()) && !groups.contains(ServiceValue.BTB.getKey()) && ServiceValue.BTB.getKey().equalsIgnoreCase(service)) {
            throw new UserBanException();
        }

        if(groups.contains(ServiceValue.BTB.getKey()) && !groups.contains(ServiceValue.CTB.getKey()) && (ServiceValue.CTB_0.getKey().equalsIgnoreCase(service) || ServiceValue.CTB_1.getKey().equalsIgnoreCase(service))) {
            throw new UserBanException();
        }

        return true;
    }
}
