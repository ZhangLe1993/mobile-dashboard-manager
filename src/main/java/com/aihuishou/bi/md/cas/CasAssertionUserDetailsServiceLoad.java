package com.aihuishou.bi.md.cas;

import com.aihuishou.bi.md.Application;
import com.aihuishou.bi.md.front.auth.UserService;
import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

import java.sql.SQLException;
import java.util.Map;

public class CasAssertionUserDetailsServiceLoad extends AbstractCasAssertionUserDetailsService {
    @Override
    protected UserDetails loadUserDetails(Assertion assertion) {
        UserService user = Application.ctx.getBean(UserService.class);
        AttributePrincipal p = assertion.getPrincipal();
        Map attr = p.getAttributes();
        try {
            if(!user.findByObId(p.getName()).getIsAdmin()){//非admin用户不能通过单点登录
                return null;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new UserDetailsObj(p.getName(), attr.get("name").toString(), attr.get("email").toString());
    }
}
