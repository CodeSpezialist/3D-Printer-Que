package com.printer.fileque.tools;

import com.printer.fileque.services.QueManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileCreator {

    private static final Logger logger = LoggerFactory.getLogger(QueManager.class);

    public static File createFile(byte[] data, String fileName) throws IOException {
        File file = new File("tempFiles/" + fileName);

        try (FileOutputStream fos = new FileOutputStream(file)) {
            fos.write(data);
            logger.info("Datei erfolgreich erstellt: " + file.getAbsolutePath());
        } catch (IOException e) {
            logger.error("File kann nicht erstellt werden: " + fileName);
            throw e;
        }
        return file;
    }
}
