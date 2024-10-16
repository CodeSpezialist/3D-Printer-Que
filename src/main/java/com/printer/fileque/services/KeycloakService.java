package com.printer.fileque.services;

import com.printer.fileque.dtos.KeycloakTokenResponse;
import com.printer.fileque.dtos.KeycloakUserInfoResponseDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KeycloakService {

    private final RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(KeycloakService.class);

    @Autowired
    public KeycloakService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Value("${keycloak.auth-server-url}")
    private String keycloakUrl;

    @Value("${keycloak.realm}")
    private String realmName;

    @Value("${keycloak.resource}")
    private String frontendClientId;

    public KeycloakTokenResponse loginAndGetToken(String userName, String password) {
        String fullApiUrl = keycloakUrl + "/realms/" + realmName + "/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("grant_type", "password");
        data.add("client_id", frontendClientId);
        data.add("username", userName);
        data.add("password", password);

        HttpEntity<MultiValueMap<String, String>> requestData = new HttpEntity<>(data, headers);

        try {
            ResponseEntity<KeycloakTokenResponse> response = restTemplate.postForEntity(fullApiUrl, requestData, KeycloakTokenResponse.class);
            return response.getBody();
        } catch (RestClientException e) {
            logger.error("Ein Fehler ist aufgetreten: ", e);
            return null;
        }
    }

    public KeycloakUserInfoResponseDto getUserInfoFromKeycloak(String authToken) {
        // Definiere die URL für den Userinfo-Endpunkt
        String userInfoUrl = keycloakUrl + "/realms/" + realmName + "/protocol/openid-connect/userinfo";

        // Setze die Header für die Anfrage
        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(authToken);

        // Erstelle die Anfrage mit Headern
        HttpEntity<String> entity = new HttpEntity<>(headers);

        // Verwende RestTemplate, um die Anfrage zu senden
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<KeycloakUserInfoResponseDto> response = restTemplate.exchange(
                userInfoUrl, HttpMethod.GET, entity, KeycloakUserInfoResponseDto.class);

        // Überprüfe, ob die Anfrage erfolgreich war und gib die Benutzerinformationen zurück
        if (response.getStatusCode().is2xxSuccessful()) {
            return response.getBody();
        } else {
            // Handle Fehlerfall (z.B. throw Exception oder return null)
            throw new RuntimeException("Failed to fetch user info: " + response.getStatusCode());
        }
    }
}
