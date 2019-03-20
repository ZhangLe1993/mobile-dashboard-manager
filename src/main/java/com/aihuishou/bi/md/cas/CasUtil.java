package com.aihuishou.bi.md.cas;

import org.springframework.security.core.context.SecurityContextHolder;

/**
 * OA单点登录工具类
 * User: jian.sun
 * Date: 15-7-16
 * Time: 下午2:48
 * To change this template use File | Settings | File Templates.
 */
public class CasUtil {


    public static String getId() {
        UserDetailsObj user = (UserDetailsObj) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getId();
    }

    public static String getUserName() {
        UserDetailsObj user = (UserDetailsObj) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        return user.getUsername();
    }

}
