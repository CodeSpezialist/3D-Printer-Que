package com.printer.fileque.entities;

import com.printer.fileque.tools.TokenGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Calendar;
import java.util.Date;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "access_tokens")
public class AccessToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String accessToken;

    private String email;

    private Date expirationDate;

    public AccessToken(String email) {
        this.accessToken = TokenGenerator.createAccessToken();
        this.email = email;
        this.expirationDate = createValidForOneYear();
    }

    private static Date createValidForOneYear() {
        Calendar calendar = Calendar.getInstance(); // Aktuelles Datum
        calendar.add(Calendar.YEAR, 1); // Ein Jahr hinzufügen
        return calendar.getTime(); // Datum als Date-Objekt zurückgeben
    }
}
