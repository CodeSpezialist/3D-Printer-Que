package com.printer.fileque.controller;

import com.printer.fileque.entities.FileQueCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/que")
public class FileQueController {

    private final FileQueCollection fileQueCollection;

    @Autowired
    public FileQueController(FileQueCollection fileQueCollection) {
        this.fileQueCollection = fileQueCollection;
    }

    @PostMapping("/add")
    public ResponseEntity<String> addFileToQue(@RequestBody String filename) {
        fileQueCollection.addToPrintQue(filename);
        return ResponseEntity.ok("File added to queue: " + filename);
    }
}
