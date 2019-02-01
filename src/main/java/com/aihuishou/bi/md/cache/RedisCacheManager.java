package com.aihuishou.bi.md.cache;

import org.springframework.cache.Cache;
import org.springframework.cache.support.AbstractCacheManager;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

@Component("cacheManager")
public class RedisCacheManager extends AbstractCacheManager {
    private Collection<? extends RedisCache> caches= Collections.synchronizedList(new ArrayList<>());

    /**
     * Specify the collection of Cache instances to use for this CacheManager.
     */
    public void setCaches(Collection<? extends RedisCache> caches) {
        this.caches = caches;
    }

    @Override
    protected Collection<? extends Cache> loadCaches() {
        return this.caches;
    }

    @Override
    protected Cache getMissingCache(String name) {
        return new RedisCache(name);
    }
}
