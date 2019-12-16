package com.aihuishou.bi.md;

import com.aihuishou.bi.md.front.auth.ActiveChecker;
import com.aihuishou.bi.md.front.auth.CurrentUserResolver;
import com.aihuishou.bi.md.front.auth.GroupInterceptor;
import com.aihuishou.bi.md.front.auth.SidChecker;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;
import java.util.List;
@EnableCaching
@EnableRedisHttpSession
@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
    @Resource
    private SidChecker sidChecker;
    @Resource
    private ActiveChecker activeChecker;

    @Resource
    private GroupInterceptor groupInterceptor;

    @Resource
    private CurrentUserResolver currentUserResolver;

    @Override
    protected void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(currentUserResolver);
    }

    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/back/**").addResourceLocations("classpath:/");
    }

    @Override
    protected void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(sidChecker).addPathPatterns("/front/**").excludePathPatterns("/front/login", "/front/active");
        registry.addInterceptor(activeChecker).addPathPatterns("/front/**").excludePathPatterns("/front/login", "/front/active");
        registry.addInterceptor(groupInterceptor).addPathPatterns("/front/gmv/**");
    }
}
