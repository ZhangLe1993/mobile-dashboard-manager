package com.aihuishou.bi.md;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;

@Configuration
public class Config {

    @Bean
    @Primary
    @ConfigurationProperties(prefix = "spring.datasource")
    public DataSource primaryDataSource() {
        return new DruidDataSource();
    }

    @Bean(name = "gp")
    @ConfigurationProperties(prefix = "spring.gp-datasource")
    public DataSource secondDataSource() {
        return new DruidDataSource();
    }

}
