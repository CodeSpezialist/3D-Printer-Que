package com.printer.fileque.services;

import com.printer.fileque.entities.FileQueCollection;
import com.printer.fileque.enums.Endpoints;
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

public class QueManager {

    private static final Logger logger = LoggerFactory.getLogger(QueManager.class);

    private static final int WAIT_TIME_MS = 30000; // 30 Sekunden

    private final RestTemplate restTemplate = new RestTemplate();
    private final FileQueCollection fileQueCollection;

    private final String octoprintApiUrl;
    private final String octoprintApiKey;

    public QueManager(String octoprintApiUrl, String octoprintApiKey, FileQueCollection fileQueCollection) {
        this.octoprintApiUrl = octoprintApiUrl;
        this.octoprintApiKey = octoprintApiKey;
        this.fileQueCollection = fileQueCollection;

        // Beispielhafte Dateien zur Warteschlange hinzufügen
        String exampleFilePath = "C:/Users/kenod/OneDrive/3D Drucker/9,7mmGewinde.gcode";
        fileQueCollection.addToPrintQue(exampleFilePath);
        fileQueCollection.addToPrintQue(exampleFilePath);
        fileQueCollection.addToPrintQue(exampleFilePath);
    }

    public void managePrintQueue() throws InterruptedException {
        while (!fileQueCollection.isQueEmpty()) {
            if (!isPrinterConnected()) {
                logger.warn("Drucker ist nicht verbunden. Warte auf Verbindung...");
                Thread.sleep(WAIT_TIME_MS); // Warten, bevor erneut überprüft wird
                continue; // Nächste Iteration der Schleife
            }

            PrinterState status = getPrinterStatus();
            logger.info("Printer Status: {}", status);

            switch (status) {
                case OPERATIONAL:
                    String nextFile = fileQueCollection.getNexFile();  // Nächste Datei aus der Warteschlange holen
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

    private boolean isPrinterConnected() {
        HttpHeaders headers = createHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);


        String url = octoprintApiUrl + Endpoints.CONNECTION_STATUS.getUrl();
        try {
            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String responseBody = response.getBody();
            if (responseBody != null && responseBody.contains("\"current\":")) {
                return responseBody.contains("\"current\":true");
            }
        } catch (RestClientException e) {
            logger.error("Fehler beim Abrufen des Verbindungsstatus: {}", e.getMessage());
        }

        return false; // Standardwert, wenn der Status nicht abgerufen werden kann
    }

    private PrinterState getPrinterStatus() {
        HttpHeaders headers = createHeaders();
        HttpEntity<String> entity = new HttpEntity<>(headers);

        ResponseEntity<String> response = restTemplate.exchange(
                octoprintApiUrl + Endpoints.JOB_STATUS.getUrl(),
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

    private void startPrint(String filename) {
        File file = new File(filename);

        if (!file.exists() || !file.canRead()) {
            logger.error("Fehler: Die Datei {} existiert nicht oder kann nicht gelesen werden.", filename);
            return; // Abbrechen, wenn die Datei ungültig ist
        }

        // Retry-Logik für Datei-Upload
        while (true) {
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(octoprintApiUrl + Endpoints.FILE_UPLOAD.getUrl());
                post.setHeader("Authorization", "Bearer " + octoprintApiKey);

                MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                        .addBinaryBody("file", file, ContentType.DEFAULT_BINARY, filename)
                        .addTextBody("select", "true", ContentType.TEXT_PLAIN)
                        .addTextBody("print", "true", ContentType.TEXT_PLAIN);

                post.setEntity(builder.build());

                try (CloseableHttpResponse response = httpClient.execute(post)) {
                    if (response.getCode() >= 200 && response.getCode() < 300) {
                        logger.info("File upload successfully and print started for file: {}", filename);
                        return; // Erfolgreich, Schleife beenden
                    } else {
                        logger.error("Can't upload and print file: {}", filename);
                    }
                }
            } catch (Exception e) {
                logger.error("Fehler beim Hochladen der Datei: {}", e.getMessage());
            }

            // Wartezeit vor erneutem Versuch
            try {
                Thread.sleep(WAIT_TIME_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                logger.error("Interrupted while waiting to retry file upload: {}", ie.getMessage());
                break; // Bei Unterbrechung aus der Schleife ausbrechen
            }
        }
    }

    private void sendGCodeCommands(String[] gcodeCommands) {
        // Retry-Logik für G-Code-Befehle
        while (true) {
            try {
                HttpHeaders headers = createHeaders();
                headers.setContentType(MediaType.APPLICATION_JSON);

                String payload = createGCodePayload(gcodeCommands);
                HttpEntity<String> entity = new HttpEntity<>(payload, headers);

                restTemplate.postForEntity(octoprintApiUrl + Endpoints.PRINTER_COMMAND.getUrl(), entity, String.class);
                logger.info("G-Code Befehle gesendet: {}", String.join(", ", gcodeCommands));
                return; // Erfolgreich, Schleife beenden
            } catch (HttpClientErrorException.Conflict e) {
                logger.error("Konflikt beim Senden der G-Code Befehle: Der Drucker ist nicht betriebsbereit. Antwort: {}", e.getResponseBodyAsString());
                // Mögliche Wartezeit vor erneutem Versuch
            } catch (RestClientException e) {
                logger.error("Fehler beim Senden der G-Code Befehle: {}", e.getMessage(), e);
            }

            // Wartezeit vor erneutem Versuch
            try {
                Thread.sleep(WAIT_TIME_MS);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                logger.error("Interrupted while waiting to retry G-Code commands: {}", ie.getMessage());
                break; // Bei Unterbrechung aus der Schleife ausbrechen
            }
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
