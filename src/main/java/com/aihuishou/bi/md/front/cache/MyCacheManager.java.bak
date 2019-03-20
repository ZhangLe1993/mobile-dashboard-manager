package com.aihuishou.bi.md.front.cache;

import org.springframework.cache.Cache;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.cache.RedisCacheWriter;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MyCacheManager extends RedisCacheManager {

    private Map<String,Cache> cacheMap=new ConcurrentHashMap();

    private RedisCacheWriter cacheWriter;
    private RedisCacheConfiguration defaultCacheConfiguration;

    public MyCacheManager(RedisCacheWriter cacheWriter, RedisCacheConfiguration defaultCacheConfiguration) {
        super(cacheWriter, defaultCacheConfiguration);
        this.cacheWriter=cacheWriter;
        this.defaultCacheConfiguration=defaultCacheConfiguration;
    }

    @Override
    public Cache getCache(String name) {
        cacheMap.putIfAbsent(name,new MyCache(name,cacheWriter,defaultCacheConfiguration));
        return cacheMap.get(name);
    }

    @Override
    public Collection<String> getCacheNames() {
        return cacheMap.keySet();
    }
}
