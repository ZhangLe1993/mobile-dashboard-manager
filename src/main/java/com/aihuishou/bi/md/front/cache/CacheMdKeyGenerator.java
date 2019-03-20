package com.aihuishou.bi.md.front.cache;

import org.springframework.cache.interceptor.KeyGenerator;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.concurrent.Callable;

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

        new Callable<Object>() {
            @Override
            public Object call() throws Exception {
                return method.invoke(o,objects);
            }
        };

        return key.toString();
    }
}
