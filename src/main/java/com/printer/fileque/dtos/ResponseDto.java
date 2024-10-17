package com.printer.fileque.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@Schema(hidden = true)
public class ResponseDto<T> {
    @Schema(description = "Gibt an, ob die Operation erfolgreich war")
    private boolean success;
    @Schema(description = "Die Nachricht zur Operation", example = "Deine Datei wurde erfolgreich in die Queue hinzugefügt.")
    private String message;
    @Schema(description = "Die Daten der Antwort")
    private T data;
}
