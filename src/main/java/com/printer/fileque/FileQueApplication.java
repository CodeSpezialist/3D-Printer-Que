package com.printer.fileque;

import com.printer.fileque.entities.FileQueCollection;
import com.printer.fileque.repos.PrintFileRepo;
import com.printer.fileque.services.MinioService;
import com.printer.fileque.services.QueManager;
import org.springframework.beans.factory.annotation.Autowired;
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

    private final FileQueCollection fileQueCollection;
    private final PrintFileRepo printFileRepo;
    private final MinioService minioService;

    @Autowired
    public FileQueApplication(FileQueCollection fileQueCollection, PrintFileRepo printFileRepo, MinioService minioService) {
        this.fileQueCollection = fileQueCollection;
        this.printFileRepo = printFileRepo;
        this.minioService = minioService;
    }

    public static void main(String[] args) {
        SpringApplication.run(FileQueApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        QueManager queManager = new QueManager(octoprintApiUrl, octoprintApiKey, fileQueCollection, printFileRepo, minioService);
        queManager.managePrintQueue();
    }
}
