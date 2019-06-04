package com.aihuishou.bi.md.front.cache;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Caching(
        cacheable = {
                @Cacheable(value = CacheHolder.CACHE_NAME, keyGenerator = "md-key-generator")
        }
)
public @interface CacheMd {
}
