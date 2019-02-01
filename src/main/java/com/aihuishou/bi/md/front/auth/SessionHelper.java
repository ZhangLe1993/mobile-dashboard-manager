package com.aihuishou.bi.md.front.auth;

import org.springframework.cache.annotation.CacheConfig;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

@Component
@CacheConfig(cacheNames = "session-helper")
public class SessionHelper {


    @CachePut(key = "#sid")//TODO 缓存需要失效时间，最好缓存在redis里
    public String bindSid(String sid, String openId) {
        return openId;
    }

    @Cacheable
    public String getOpenId(String sid) {
        return null;
    }
}
