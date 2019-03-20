package com.aihuishou.bi.md.front.cache;

import org.springframework.data.redis.cache.RedisCache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheWriter;

public class MyCache extends RedisCache {


    protected MyCache(String name, RedisCacheWriter cacheWriter, RedisCacheConfiguration cacheConfig) {
        super(name, cacheWriter, cacheConfig);
    }
    @Override
    public ValueWrapper get(Object key){
        ValueWrapper v = super.get(key);
        return v;
    }
}
