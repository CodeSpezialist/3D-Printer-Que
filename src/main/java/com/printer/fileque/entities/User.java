package com.printer.fileque.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {

    @Id
    private String email;

    private String firstName;

    private String lastName;

    private boolean isActive;

    private String keycloakId;

    public User(String email, String firstName, String lastName) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.isActive = true;
    }

    //Konstruktor für JWT User Sync
    public User(String email, String firstName, String lastName, String keycloakId) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.keycloakId = keycloakId;
        this.isActive = true;
    }
}
