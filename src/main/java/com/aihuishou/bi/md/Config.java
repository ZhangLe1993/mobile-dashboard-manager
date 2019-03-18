package com.aihuishou.bi.md;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;

@Configuration
public class Config {
    @Value("${gp.url}")
    private String gpUrl;
    @Value("${gp.username}")
    private String gpUser;
    @Value("${gp.password}")
    private String gpPw;
    @Value("${gp.driver-class-name}")
    private String driverClassName;

    @Bean(name = "gp")
    public DataSource gp(){
        DruidDataSource gp=new DruidDataSource();
        gp.setUrl(gpUrl);
        gp.setUsername(gpUser);
        gp.setPassword(gpPw);
        gp.setDriverClassName(driverClassName);
        return gp;
    }
}
