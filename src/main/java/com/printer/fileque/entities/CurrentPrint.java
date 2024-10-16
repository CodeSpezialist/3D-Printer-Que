package com.printer.fileque.entities;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "current_print")
@Schema(hidden = true)
public class CurrentPrint {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @ManyToOne
    @JoinColumn(name = "print_file")
    private PrintFile printFile;

    @Temporal(TemporalType.TIMESTAMP)
    private Date startTime;

    public CurrentPrint(PrintFile printFile) {
        this.printFile = printFile;
        this.startTime = new Date();
    }
}
