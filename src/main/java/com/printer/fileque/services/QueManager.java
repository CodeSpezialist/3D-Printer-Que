package com.printer.fileque.services;

import com.printer.fileque.enums.PrinterState;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.mime.MultipartEntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

public class QueManager {

    private static final Logger logger = LoggerFactory.getLogger(QueManager.class);

    private final RestTemplate restTemplate = new RestTemplate();
    private final Queue<String> printQue = new LinkedList<>();

    private final String octoprintApiUrl;
    private final String octoprintApiKey;

    public QueManager(String octoprintApiUrl, String octoprintApiKey) {
        this.octoprintApiUrl = octoprintApiUrl;
        this.octoprintApiKey = octoprintApiKey;

        printQue.add("C:/Users/kenod/OneDrive/3D Drucker/9,7mmGewinde.gcode");
        printQue.add("C:/Users/kenod/OneDrive/3D Drucker/9,7mmGewinde.gcode");
        printQue.add("C:/Users/kenod/OneDrive/3D Drucker/9,7mmGewinde.gcode");
    }

    public void managePrintQueue() throws InterruptedException {
        while (!printQue.isEmpty()) {
            PrinterState status = getPrinterStatus();

            logger.info("Printer Status: " + status.toString());

            if (status == PrinterState.OPERATIONAL) {
                String nextFile = printQue.poll();  // Nächste Datei aus der Warteschlange holen
                logger.info("Starte Druck: " + nextFile);
                startPrint(nextFile);
            }
            if (status == PrinterState.FINISHED) {
                logger.info("Druck abgeschlossen! Schiebe Druck vom Bett.");
                sendGCodeCommands(new String[]{"G0 X110 Y220 Z5", "G0 Y0", "G0 Y220"});
            }
            if (status == PrinterState.UNKNOWN) {
                logger.info("Druckerstatus: " + status + ", warte auf freien Drucker...");
            }

            Thread.sleep(30000);  // 30 Sekunden warten zwischen Statusabfragen
        }
    }

    private PrinterState getPrinterStatus() {
        HttpHeaders headers = new HttpHeaders();

        headers.set("X-Api-Key", octoprintApiKey);

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                octoprintApiUrl + "/job",
                HttpMethod.GET,
                entity,
                String.class
        );

        String responseBody = response.getBody();

        if (responseBody != null && responseBody.contains("\"state\":")) {
            return responseBody.contains("\"Operational\"") ? PrinterState.OPERATIONAL : PrinterState.FINISHED;
        }

        return PrinterState.UNKNOWN;
    }

    private boolean startPrint(String filename) {
        File file = new File(filename);

        // Überprüfen, ob die Datei existiert und lesbar ist
        if (!file.exists() || !file.canRead()) {
            logger.error("Fehler: Die Datei " + filename + " existiert nicht oder kann nicht gelesen werden.");
            return false;
        }

        try {
            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpPost post = new HttpPost(octoprintApiUrl + "/files/local");

            post.setHeader("Authorization", "Bearer " + octoprintApiKey);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create();
            builder.addBinaryBody("file", file, ContentType.DEFAULT_BINARY, filename);

            // Diese beiden Textfelder setzen, um den Druck zu starten
            builder.addTextBody("select", "true", ContentType.TEXT_PLAIN);
            builder.addTextBody("print", "true", ContentType.TEXT_PLAIN);

            post.setEntity(builder.build());

            // Anfrage senden und Antwort empfangen
            CloseableHttpResponse response = httpClient.execute(post);

            if (response.getCode() >= 200 && response.getCode() < 300) {
                logger.info("File upload successfully and print started for File: " + filename);
                httpClient.close();
                return true;
            } else {
                logger.error("Can't upload and print file: " + filename);
                httpClient.close();
                return false;
            }
        } catch (Exception e) {
            logger.error("Fehler beim Hochladen der Datei: " + e.getMessage());
            return false;
        }
    }


    private void sendGCodeCommands(String[] gcodeCommands) {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", octoprintApiKey);
        headers.set("Content-Type", "application/json");

        StringBuilder commandPayload = new StringBuilder("{\"commands\":[");
        for (int i = 0; i < gcodeCommands.length; i++) {
            commandPayload.append("\"").append(gcodeCommands[i]).append("\"");
            if (i < gcodeCommands.length - 1) {
                commandPayload.append(",");
            }
        }
        commandPayload.append("]}");

        HttpEntity<String> entity = new HttpEntity<>(commandPayload.toString(), headers);

        // G-Code Befehle senden
        restTemplate.postForEntity(octoprintApiUrl + "/printer/command", entity, String.class);
        logger.info("G-Code Befehle gesendet: " + String.join(", ", gcodeCommands));
    }


}
