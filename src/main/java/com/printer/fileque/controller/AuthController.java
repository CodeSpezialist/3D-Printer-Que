package com.printer.fileque.controller;

import com.printer.fileque.dtos.KeycloakTokenResponse;
import com.printer.fileque.dtos.KeycloakUserInfoResponseDto;
import com.printer.fileque.dtos.LoginDto;
import com.printer.fileque.dtos.ResponseDto;
import com.printer.fileque.entities.AccessToken;
import com.printer.fileque.services.AccessTokenService;
import com.printer.fileque.services.KeycloakService;
import com.printer.fileque.tools.ResponseDtoCreator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/token")
@CrossOrigin(origins = "*")
@Tag(name = "Auth Controller", description = "Controller für die Authentifizierung und Registrierung von Usern")
public class AuthController {

    private final KeycloakService keycloakService;
    private final AccessTokenService accessTokenService;

    @Autowired
    public AuthController(KeycloakService keycloakService, AccessTokenService accessTokenService) {
        this.keycloakService = keycloakService;
        this.accessTokenService = accessTokenService;
    }

    @PostMapping("/generate")
    @Operation(summary = "Access Token generieren", description = "Generiert einen Access Token, mit welcher auf die API der PrinterQue zugriffen werden kann.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Erfolgreich abgerufen"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    public ResponseEntity<ResponseDto<AccessToken>> login(@RequestBody LoginDto loginDto) {
        KeycloakTokenResponse tokenResponse = keycloakService.loginAndGetToken(loginDto.getUserName(), loginDto.getPassword());
        KeycloakUserInfoResponseDto userInfoResponseDto = keycloakService.getUserInfoFromKeycloak(tokenResponse.getAccess_token());
        if (tokenResponse.getExpires_in() > 10) {
            AccessToken accessToken = accessTokenService.createNewAccessToken(userInfoResponseDto.getEmail());
            ResponseDto<AccessToken> responseDto = ResponseDtoCreator.createResponseDto(accessToken);
            return ResponseEntity.ok(responseDto);
        } else {
            ResponseDto<AccessToken> responseDto = ResponseDto.<AccessToken>builder()
                    .success(false)
                    .message("Anmeldedaten sind falsch,bitte überprüfen sie diese!")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.UNAUTHORIZED);
        }
    }
}
