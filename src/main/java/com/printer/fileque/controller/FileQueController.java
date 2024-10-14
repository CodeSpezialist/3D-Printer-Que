package com.printer.fileque.controller;

import com.printer.fileque.entities.FileQueCollection;
import com.printer.fileque.entities.PrintFile;
import com.printer.fileque.repos.PrintFileRepo;
import com.printer.fileque.services.QueManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/que")
public class FileQueController {

    private final FileQueCollection fileQueCollection;
    private final PrintFileRepo printFileRepo;
    private final QueManager queManager;

    @Autowired
    public FileQueController(FileQueCollection fileQueCollection, PrintFileRepo printFileRepo, QueManager queManager) {
        this.fileQueCollection = fileQueCollection;
        this.printFileRepo = printFileRepo;
        this.queManager = queManager;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addFileToQue(@RequestBody String filename) {

        PrintFile printFile = printFileRepo.save(new PrintFile(filename));
        fileQueCollection.addToPrintQue(printFile);

        new Thread(() -> {
            try {
                queManager.managePrintQueue();
            }catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }).start();

        return ResponseEntity.ok("File added to queue: " + filename);
    }
}
