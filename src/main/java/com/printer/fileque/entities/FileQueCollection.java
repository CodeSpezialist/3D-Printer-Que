package com.printer.fileque.entities;

import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

@Component
public class FileQueCollection {

    private final Queue<String> printQue = new LinkedList<>();

    public void addToPrintQue(String filename) {
        printQue.add(filename);
    }

    public String getNexFile(){
        return printQue.poll();
    }

    public boolean isQueEmpty(){
        return printQue.isEmpty();
    }
}
