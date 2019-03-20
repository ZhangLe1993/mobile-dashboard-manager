package com.aihuishou.bi.md.cas;

import org.jasig.cas.client.authentication.AttributePrincipal;
import org.jasig.cas.client.validation.Assertion;
import org.springframework.security.cas.userdetails.AbstractCasAssertionUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Map;

public class CasAssertionUserDetailsServiceLoad extends AbstractCasAssertionUserDetailsService {
    @Override
    protected UserDetails loadUserDetails(Assertion assertion) {
        AttributePrincipal p = assertion.getPrincipal();
        Map attr = p.getAttributes();
        return new UserDetailsObj(p.getName(), attr.get("name").toString(), attr.get("email").toString());
    }
}
