package com.printer.fileque.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "Login Daten für Token Generierung.")
public class LoginDto {

    @Schema(description = "E-Mail Adresse mit welcher du für das Tool registriert bist.", example = "test@test.de")
    private String userName;
    @Schema(description = "Dein vergebenes Passwort", example = "Password+1")
    private String password;


}
