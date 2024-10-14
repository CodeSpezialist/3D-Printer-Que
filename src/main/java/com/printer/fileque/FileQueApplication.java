package com.printer.fileque;

import com.printer.fileque.entities.FileQueCollection;
import com.printer.fileque.services.QueManager;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.CrossOrigin;

@SpringBootApplication
@CrossOrigin(origins = "*")
public class FileQueApplication implements CommandLineRunner {

    @Value("${octoprint.api-url}")
    private String octoprintApiUrl;

    @Value("${octoprint.api-key}")
    private String octoprintApiKey;

    private final FileQueCollection fileQueCollection = new FileQueCollection();

    public static void main(String[] args) {
        SpringApplication.run(FileQueApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        // Keine Instanziierung hier
        QueManager queManager = new QueManager(octoprintApiUrl, octoprintApiKey, fileQueCollection); // Initialisierung hier
        queManager.managePrintQueue();
    }
}
