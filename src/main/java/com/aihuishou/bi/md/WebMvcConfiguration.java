package com.aihuishou.bi.md;

import com.aihuishou.bi.md.front.auth.CurrentUserResolver;
import com.aihuishou.bi.md.front.auth.SidChecker;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import javax.annotation.Resource;
import java.util.List;

@Configuration
public class WebMvcConfiguration extends WebMvcConfigurationSupport {
    @Resource
    private SidChecker sidChecker;

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
        registry.addInterceptor(sidChecker).addPathPatterns("/front/**").excludePathPatterns("/front/login");
    }
}
