package com.printer.fileque.entities;

import com.printer.fileque.dtos.NewPrintFileDto;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Table(name = "print_files")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
public class PrintFile {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private UUID orderId;

    private String fileName;


    public PrintFile(NewPrintFileDto printFileDto) {
        this.orderId = printFileDto.getOrderId();
        this.fileName = printFileDto.getFilename();
    }
}
