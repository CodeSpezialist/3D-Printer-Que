package com.printer.fileque.controller;

import com.printer.fileque.dtos.KeycloakTokenResponse;
import com.printer.fileque.dtos.LoginDto;
import com.printer.fileque.dtos.ResponseDto;
import com.printer.fileque.entities.User;
import com.printer.fileque.services.KeycloakService;
import com.printer.fileque.services.UserService;
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
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
@Tag(name = "Auth Controller", description = "Controller für die Authentifizierung und Registrierung von Usern")
public class AuthController {

    private final KeycloakService keycloakService;
    private final UserService userService;

    @Autowired
    public AuthController(KeycloakService keycloakService, UserService userService) {
        this.keycloakService = keycloakService;
        this.userService = userService;
    }

    @PostMapping("/login")
    @Operation(summary = "Den User anmelden", description = "Ermöglicht den Login eines Nutzers und gibt einen Token zurück!")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Erfolgreich abgerufen"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    public ResponseEntity<ResponseDto<KeycloakTokenResponse>> login(@RequestBody LoginDto loginDto) {
        KeycloakTokenResponse tokenResponse = keycloakService.loginAndGetToken(loginDto.getUserName(), loginDto.getPassword());
        if (tokenResponse != null) {
            ResponseDto<KeycloakTokenResponse> responseDto = ResponseDtoCreator.createResponseDto(tokenResponse);
            return ResponseEntity.ok(responseDto);
        } else {
            ResponseDto<KeycloakTokenResponse> responseDto = ResponseDto.<KeycloakTokenResponse>builder()
                    .success(false)
                    .message("Anmeldedaten sind falsch,bitte überprüfen sie diese!")
                    .data(null)
                    .build();
            return new ResponseEntity<>(responseDto, HttpStatus.UNAUTHORIZED);
        }
    }

    @GetMapping("/getUserData")
    @Operation(summary = "Load User Data", description = "Ermöglicht es die Daten des aktuell angemeldeten Users zu laden")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Erfolgreich abgerufen"),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "403", description = "Forbidden", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(schema = @Schema(implementation = ResponseDto.class))),
    })
    public ResponseEntity<ResponseDto<User>> loadUserData() {
        User user = userService.getLoggedUser();
        ResponseDto<User> responseDto = ResponseDtoCreator.createResponseDto(user);
        return ResponseEntity.ok(responseDto);
    }
}
