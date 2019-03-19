package com.aihuishou.bi.md.back;

import com.aihuishou.bi.md.cache.RedisCache;
import com.aihuishou.bi.md.cache.RedisConUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/back")
public class HomeC {

    @RequestMapping("/clear_md")
    public void clearCache(){
        RedisConUtils.delPrefix(RedisCache.PREFIX_CACHE_KEY);
    }
}
