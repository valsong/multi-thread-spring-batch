package com.valsong.multithreadspringbatch.configuration;

import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

import javax.sql.DataSource;

@Configuration
@EnableJpaAuditing
public class DataSourceConfiguration {

    @Bean(initMethod = "init", destroyMethod = "close")
    @ConfigurationProperties(prefix="spring.datasource.test")
    public DataSource druidDataSource() {
        return new DruidDataSource();
    }

}
