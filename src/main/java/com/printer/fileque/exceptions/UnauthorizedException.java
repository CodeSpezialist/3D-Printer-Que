package com.printer.fileque.exceptions;

public class UnauthorizedException extends RuntimeException{

    public UnauthorizedException(){
        super("You are not authorized to do this!");
    }
}
