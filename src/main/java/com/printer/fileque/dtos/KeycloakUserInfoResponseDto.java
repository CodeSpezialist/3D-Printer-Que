package com.printer.fileque.dtos;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
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
