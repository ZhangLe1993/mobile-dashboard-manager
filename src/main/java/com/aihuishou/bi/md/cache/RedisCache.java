package com.aihuishou.bi.md.cache;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.SimpleKey;
import org.springframework.cache.support.SimpleValueWrapper;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Callable;

public class RedisCache implements Cache {

    private Logger logger = LoggerFactory.getLogger(RedisCache.class);

    public static final String PREFIX_CACHE_KEY = "md_cache_";
    private static final Long EXPIRE_SECOND = 60 * 60L;//缓存失效时间 默认1h
    private String name;

    public RedisCache(String name) {
        this.name = name;
    }

    public RedisCache() {
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Object getNativeCache() {
        return null;
    }

    @Override
    public ValueWrapper get(Object key) {
        if (key == null) {
            return null;
        }
        String cacheKey = getCacheKey(key);
        Map<String, String> vMap = RedisConUtils.hmget(cacheKey);
        if (vMap != null && vMap.size() > 0) {
            return new SimpleValueWrapper(getV(vMap));
        } else {
            return null;
        }
    }

    @Override
    public <T> T get(Object key, Class<T> type) {
        if (key == null) {
            return null;
        }
        String cacheKey = getCacheKey(key);
        Map<String, String> vMap = RedisConUtils.hmget(cacheKey);
        if (type == null) {
            throw new RuntimeException("type cannot be null");
        }
        try {
            if (vMap != null && vMap.size() > 0) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readerFor(type).readValue(vMap.get("val"));
            }
        } catch (Exception e) {
            logger.error("", e);
        }
        return null;
    }

    @Override
    public <T> T get(Object key, Callable<T> valueLoader) {
        if (key == null) {
            return null;
        }
        String cacheKey = getCacheKey(key);
        Map<String, String> vMap = RedisConUtils.hmget(cacheKey);
        try {
            if (vMap == null || vMap.size() == 0) {
                T v = valueLoader.call();
                put(key, v);
                return v;
            } else {
                return (T) getV(vMap);
            }
        } catch (Exception e) {
            logger.error("", e);
            return null;
        }
    }

    private Object getV(Map<String, String> vMap) {
        try {
            Class type = Class.forName(vMap.get("type").toString());
            String data = vMap.get("val");
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readerFor(type).readValue(data);
        } catch (ClassNotFoundException | IOException e) {
            logger.error("", e);
            return null;
        }
    }

    private String getCacheKey(Object key) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            if(key instanceof SimpleKey){
                return PREFIX_CACHE_KEY + name + "|" + mapper.writeValueAsString(((SimpleKey)key).toString());
            }else{
                return PREFIX_CACHE_KEY + name + "|" + mapper.writeValueAsString(key);
            }
        } catch (JsonProcessingException e) {
            logger.error("",e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void put(Object key, Object value) {
        if (key == null || value == null) {
            return;
        }
        String cacheKey = getCacheKey(key);
        Map<String, String> vMap = new HashMap<>();
        try {
            vMap.put("type", value.getClass().getTypeName());
            vMap.put("val", new ObjectMapper().writeValueAsString(value));
            synchronized (cacheKey.intern()) {
                RedisConUtils.hmset(cacheKey, vMap);
                RedisConUtils.expire(cacheKey, EXPIRE_SECOND.intValue());
            }
        } catch (JsonProcessingException e) {
            logger.error("",e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        if (key == null || value == null) {
            throw new RuntimeException("value can not be null");
        }
        String cacheKey = getCacheKey(key);
        synchronized (cacheKey.intern()) {
            Map<String, String> m = RedisConUtils.hmget(cacheKey);
            if (m != null && m.size() != 0) {
                return new SimpleValueWrapper(getV(m));
            } else {
                put(key, value);
                return new SimpleValueWrapper(value);
            }
        }
    }

    @Override
    public void evict(Object key) {
        if (key == null) {
            return;
        }
        String cacheKey = getCacheKey(key);
        RedisConUtils.removeKey(cacheKey);
    }

    @Override
    public void clear() {
        RedisConUtils.delPrefix(PREFIX_CACHE_KEY + name);
    }

    public void setName(String name) {
        this.name = name;
    }
}
