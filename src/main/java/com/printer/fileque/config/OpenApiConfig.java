package com.printer.fileque.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(
                        new Info()
                                .title("Printer Que API DOC")
                                .version("0.0.1")
                                .description("Dies ist die API Dokumentation für die Printer QUE. Hier kann ein API-Token für das Tool generiert werden und neue Files an die PrinterQue gesendet werden.\n\n Für einen Account um dich hier zu Verifizieren melde dich bei something@kenosserver.de mit deinen Daten und deiner Begründung warum du hier zugriff bekommen solltest.")
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components().addSecuritySchemes("bearerAuth",
                        new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")));
    }
}
