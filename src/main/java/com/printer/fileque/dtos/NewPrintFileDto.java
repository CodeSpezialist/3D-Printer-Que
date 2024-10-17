package com.printer.fileque.dtos;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@Schema(description = "Schema um eine neue Datei in die Queue hinzuzufügen.")
public class NewPrintFileDto {
    @Schema(description = "Bestellnummer, zu welcher die 3D Datei gehört.")
    private UUID orderId;
    @Schema(description = "Dateiname wie sie im Minio abgespeichert ist.", example = "test.gcode")
    private String filename;
}
