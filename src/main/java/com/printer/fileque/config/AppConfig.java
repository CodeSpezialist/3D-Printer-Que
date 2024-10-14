package com.printer.fileque.config;

import com.printer.fileque.entities.FileQueCollection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public FileQueCollection fileQueCollection() {
        return new FileQueCollection();
    }
}
