package com.example.demo_sftp.config.bean;

import org.apache.tika.Tika;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfig {
    @Bean
    public Tika tika() {
        return new Tika();
    }
}
