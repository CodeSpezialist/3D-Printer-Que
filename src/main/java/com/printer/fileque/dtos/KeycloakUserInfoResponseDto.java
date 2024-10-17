package com.printer.fileque.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Schema(hidden = true)
public class KeycloakUserInfoResponseDto {
    private String sub;
    private String name;
    private String preferred_username;
    private String email;
    private String given_name;
    private String family_name;
    private String picture;
    private String locale;
}
