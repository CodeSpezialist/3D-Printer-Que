package com.printer.fileque.exceptions;

public class KeycloakErrorException extends RuntimeException{
    public KeycloakErrorException(String message){
        super(message);
    }
}