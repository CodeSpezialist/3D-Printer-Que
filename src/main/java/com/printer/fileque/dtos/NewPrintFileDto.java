package com.printer.fileque.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class NewPrintFileDto {
    private UUID orderId;
    private String filename;
}
