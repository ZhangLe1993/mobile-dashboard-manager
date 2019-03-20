package com.aihuishou.bi.md.front.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

@Component("md-key-generator")
public class CacheMdKeyGenerator implements KeyGenerator {
    @Override
    public Object generate(Object o, Method method, Object... objects) {
        StringBuilder key = new StringBuilder(o.getClass().getName() + "|" + method.getName());
        Parameter[] params = method.getParameters();
        for (Parameter p : params) {
            key.append(p.getName());
            key.append("|");
        }
        for (Object v : objects) {
            key.append(v.toString());
            key.append("|");
        }
        return key.toString();
    }
}
