package com.printer.fileque.config;

import org.flywaydb.core.Flyway;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class MigrationRunner implements CommandLineRunner {

    private final Flyway flyway;

    @Autowired
    public MigrationRunner(Flyway flyway) {
        this.flyway = flyway;
    }

    @Override
    public void run(String... args) {
        flyway.migrate();
    }
}
