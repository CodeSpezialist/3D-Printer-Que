package com.printer.fileque.entities;

import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.Queue;

@Component
public class FileQueCollection {

    private final Queue<PrintFile> printQue = new LinkedList<>();

    public void addToPrintQue(PrintFile file) {
        printQue.add(file);
    }

    public PrintFile getNexFile() {
        return printQue.poll();
    }

    public boolean isQueEmpty() {
        return printQue.isEmpty();
    }
}
