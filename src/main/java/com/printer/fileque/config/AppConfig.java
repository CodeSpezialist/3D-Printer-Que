package com.printer.fileque.config;

import com.printer.fileque.entities.FileQueCollection;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class AppConfig {

    @Bean
    public FileQueCollection fileQueCollection() {
        return new FileQueCollection();
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
