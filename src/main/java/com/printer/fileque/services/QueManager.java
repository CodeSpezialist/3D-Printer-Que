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
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.File;
import java.util.LinkedList;
import java.util.Queue;

public class QueManager {

    private static final Logger logger = LoggerFactory.getLogger(QueManager.class);

    private static final int WAIT_TIME_MS = 30000; // 30 Sekunden
    private static final String FILE_UPLOAD_ENDPOINT = "/files/local";
    private static final String JOB_STATUS_ENDPOINT = "/job";
    private static final String PRINTER_COMMAND_ENDPOINT = "/printer/command";

    private final RestTemplate restTemplate = new RestTemplate();
    private final Queue<String> printQueue = new LinkedList<>();

    private final String octoprintApiUrl;
    private final String octoprintApiKey;

    public QueManager(String octoprintApiUrl, String octoprintApiKey) {
        this.octoprintApiUrl = octoprintApiUrl;
        this.octoprintApiKey = octoprintApiKey;

        // TODO Beispieldaten entfernen
        // Beispielhafte Dateien zur Warteschlange hinzufügen
        String exampleFilePath = "C:/Users/kenod/OneDrive/3D Drucker/9,7mmGewinde.gcode";
        printQueue.add(exampleFilePath);
        printQueue.add(exampleFilePath);
        printQueue.add(exampleFilePath);
    }

    public void managePrintQueue() throws InterruptedException {
        while (!printQueue.isEmpty()) {
            PrinterState status = getPrinterStatus();

            logger.info("Printer Status: {}", status);

            switch (status) {
                case OPERATIONAL:
                    String nextFile = printQueue.poll();  // Nächste Datei aus der Warteschlange holen
                    logger.info("Starte Druck: {}", nextFile);
                    startPrint(nextFile);
                    break;
                case FINISHED:
                    logger.info("Druck abgeschlossen! Schiebe Druck vom Bett.");
                    sendGCodeCommands(new String[]{"G0 X110 Y220 Z5", "G0 Y0", "G0 Y220"});
                    break;
                case UNKNOWN:
                    logger.info("Druckerstatus: {}, warte auf freien Drucker...", status);
                    break;
            }

            Thread.sleep(WAIT_TIME_MS);
        }
    }

    private PrinterState getPrinterStatus() {
        HttpHeaders headers = createHeaders();

        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                octoprintApiUrl + JOB_STATUS_ENDPOINT,
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

        if (!file.exists() || !file.canRead()) {
            logger.error("Fehler: Die Datei {} existiert nicht oder kann nicht gelesen werden.", filename);
            return false;
        }

        try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(octoprintApiUrl + FILE_UPLOAD_ENDPOINT);
            post.setHeader("Authorization", "Bearer " + octoprintApiKey);

            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .addBinaryBody("file", file, ContentType.DEFAULT_BINARY, filename)
                    .addTextBody("select", "true", ContentType.TEXT_PLAIN)
                    .addTextBody("print", "true", ContentType.TEXT_PLAIN);

            post.setEntity(builder.build());

            try (CloseableHttpResponse response = httpClient.execute(post)) {
                if (response.getCode() >= 200 && response.getCode() < 300) {
                    logger.info("File upload successfully and print started for file: {}", filename);
                    return true;
                } else {
                    logger.error("Can't upload and print file: {}", filename);
                    return false;
                }
            }
        } catch (Exception e) {
            logger.error("Fehler beim Hochladen der Datei: {}", e.getMessage());
            return false;
        }
    }

    private void sendGCodeCommands(String[] gcodeCommands) {
        try {
            HttpHeaders headers = createHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            String payload = createGCodePayload(gcodeCommands);
            HttpEntity<String> entity = new HttpEntity<>(payload, headers);

            restTemplate.postForEntity(octoprintApiUrl + PRINTER_COMMAND_ENDPOINT, entity, String.class);
            logger.info("G-Code Befehle gesendet: {}", String.join(", ", gcodeCommands));
        } catch (HttpClientErrorException.Conflict e) {
            logger.error("Konflikt beim Senden der G-Code Befehle: Der Drucker ist nicht betriebsbereit. Antwort: {}", e.getResponseBodyAsString());
            // Mögliche Reaktion: Warten, Benachrichtigung, Retry-Logik etc.
        } catch (RestClientException e) {
            logger.error("Fehler beim Senden der G-Code Befehle: {}", e.getMessage(), e);
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("X-Api-Key", octoprintApiKey);
        return headers;
    }

    private String createGCodePayload(String[] commands) {
        StringBuilder commandPayload = new StringBuilder("{\"commands\":[");
        for (int i = 0; i < commands.length; i++) {
            commandPayload.append("\"").append(commands[i]).append("\"");
            if (i < commands.length - 1) {
                commandPayload.append(",");
            }
        }
        commandPayload.append("]}");
        return commandPayload.toString();
    }
}
