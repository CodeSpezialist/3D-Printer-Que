package com.printer.fileque.controller;

import com.printer.fileque.dtos.NewPrintFileDto;
import com.printer.fileque.dtos.ResponseDto;
import com.printer.fileque.entities.FileQueCollection;
import com.printer.fileque.entities.PrintFile;
import com.printer.fileque.repos.PrintFileRepo;
import com.printer.fileque.services.AccessTokenService;
import com.printer.fileque.services.QueManager;
import com.printer.fileque.tools.ResponseDtoCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/que")
public class FileQueController {

    private static final Logger logger = LoggerFactory.getLogger(FileQueController.class);

    private final FileQueCollection fileQueCollection;
    private final PrintFileRepo printFileRepo;
    private final QueManager queManager;

    @Autowired
    public FileQueController(FileQueCollection fileQueCollection, PrintFileRepo printFileRepo, QueManager queManager, AccessTokenService accessTokenService) {
        this.fileQueCollection = fileQueCollection;
        this.printFileRepo = printFileRepo;
        this.queManager = queManager;
    }

    @PostMapping("/add")
    public ResponseEntity<ResponseDto<String>> addFileToQue(@RequestBody NewPrintFileDto printFileDto) {

        PrintFile printFile = printFileRepo.save(new PrintFile(printFileDto));
        fileQueCollection.addToPrintQue(printFile);
        logger.info("Added new File to Printer Queue: " + printFileDto.getFilename());

        new Thread(() -> {
            try {
                queManager.managePrintQueue();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).start();

        ResponseDto<String> responseDto = ResponseDtoCreator.createResponseDto("File added to queue: " + printFileDto.getFilename());
        return ResponseEntity.ok(responseDto);

    }
}
