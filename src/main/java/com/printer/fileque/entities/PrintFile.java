package com.printer.fileque.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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

    private String fileName;


    public PrintFile(String filename) {
        this.fileName = filename;
    }
}
