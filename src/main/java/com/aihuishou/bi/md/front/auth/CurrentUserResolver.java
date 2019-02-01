/*
 * <<
 * Davinci
 * ==
 * Copyright (C) 2016 - 2018 EDP
 * ==
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 * >>
 */

package com.aihuishou.bi.md.front.auth;

import com.aihuishou.bi.md.front.auth.exception.InvalidSidException;
import com.aihuishou.bi.md.front.auth.exception.SidMissException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;

/**
 * 获取当前用户
 *
 * @CurrentUser 注解 解析器
 */
@Slf4j
@Component
public class CurrentUserResolver implements HandlerMethodArgumentResolver {

    @Resource
    private SessionHelper sessionHelper;

    @Resource
    private UserService userService;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterType().isAssignableFrom(User.class)
                && parameter.hasParameterAnnotation(CurrentUser.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) {
        String sid = webRequest.getNativeRequest(HttpServletRequest.class).getHeader("sid");//request header 里存储sid
        if (!StringUtils.isEmpty(sid)) {//sid => openId => user
            String openId = sessionHelper.getOpenId(sid);
            if (StringUtils.isEmpty(openId)) {
                throw new InvalidSidException();
            }
            return userService.checkActive(openId);
        } else {
            throw new SidMissException();
        }
    }
}