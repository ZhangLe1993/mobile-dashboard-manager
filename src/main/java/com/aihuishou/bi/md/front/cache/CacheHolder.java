package com.aihuishou.bi.md.front.cache;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.lang.reflect.Method;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
public class CacheHolder {

    public static final String CACHE_NAME = "cache-md";

    public static final String GMV_LAST_DATA_DATE_CACHE_NAME = "gmv-last-data-date";

    private Map<String, Thread> cacheKeys = new ConcurrentHashMap<>();//防止有内存泄漏,需要设置上线大小,避免同样的key重复监听
    private final int MAX_MONITOR_SIZE = 30;

    @Value("#{T(java.time.Duration).parse('PT'+'${spring.cache.redis.time-to-live}')}")
    private Duration ttl;

    private static final long MIN_TIME_SLEEP = 60 * 1000;//自动重刷新最少需要等待的时间

    @Resource
    private CacheManager cacheManager;

    /**
     * 追踪该
     *
     * @param key
     */
    public void monitorCacheKey(String key, Object o, Method method, Object... objects) {
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                long time2sleep = MIN_TIME_SLEEP;
                while (true) {
                    try {
                        Thread.sleep(time2sleep);
                        log.info("cache holder refresh!!! "+key);
                        long from = System.currentTimeMillis();
                        Object v = method.invoke(o, objects);
                        time2sleep = Math.max(ttl.toMillis() - 2 * (System.currentTimeMillis() - from), MIN_TIME_SLEEP);
                        Cache cache = cacheManager.getCache(CACHE_NAME);
                        cache.put(key, v);
                    } catch (Throwable e) {
                        log.error("", e);
                    }
                }
            }
        });
        if (cacheKeys.keySet().size() <= MAX_MONITOR_SIZE && cacheKeys.putIfAbsent(key, t) == null) {
            t.start();
        }
    }
}
