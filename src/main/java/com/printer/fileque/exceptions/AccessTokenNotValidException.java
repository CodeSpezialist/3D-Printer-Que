package com.printer.fileque.exceptions;

public class AccessTokenNotValidException extends RuntimeException{

    public AccessTokenNotValidException(){
        super("Access Token is not valid!");
    }
}
