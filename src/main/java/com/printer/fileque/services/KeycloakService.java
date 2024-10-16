package com.printer.fileque.services;

import com.printer.fileque.dtos.KeycloakTokenResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.security.SecureRandom;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class KeycloakService {

    private final RestTemplate restTemplate;

    private static final Logger logger = LoggerFactory.getLogger(KeycloakService.class);

    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()-_=+<>?";
    private static final SecureRandom random = new SecureRandom();

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

    @Value("${keycloak.backend-client}")
    private String backendClientId;

    @Value("${keycloak.client-secret}")
    private String clientSecret;

    @Value("${keycloak.admin-username}")
    private String adminUserName;

    @Value("${keycloak.admin-password}")
    private String adminPassword;

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

    public boolean isTokenValid(String accessToken) {
        String fullApiUrl = keycloakUrl + "/realms/" + realmName + "/protocol/openid-connect/token/introspect";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("token", accessToken);
        data.add("client_id", backendClientId);
        data.add("client_secret", clientSecret);

        HttpEntity<MultiValueMap<String, String>> requestData = new HttpEntity<>(data, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(fullApiUrl, requestData, Map.class);
            Map responseBody = response.getBody();
            return responseBody != null && Boolean.TRUE.equals(responseBody.get("active"));
        } catch (RestClientException e) {
            logger.error("Ein Fehler ist aufgetreten: ", e);
            return false;
        }
    }

    private KeycloakTokenResponse loginAndGetTokenFromMasterRealm() {
        String masterRealmUrl = keycloakUrl + "/realms/master/protocol/openid-connect/token";

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(org.springframework.http.MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> data = new LinkedMultiValueMap<>();
        data.add("grant_type", "password");
        data.add("client_id", "admin-cli");
        data.add("username", adminUserName);
        data.add("password", adminPassword);

        HttpEntity<MultiValueMap<String, String>> requestData = new HttpEntity<>(data, headers);

        try {
            ResponseEntity<KeycloakTokenResponse> response = restTemplate.postForEntity(masterRealmUrl, requestData, KeycloakTokenResponse.class);
            return response.getBody();
        } catch (RestClientException e) {
            logger.error("Fehler beim Abrufen des Tokens für den Master-Realm: ", e);
            return null;
        }
    }
}
