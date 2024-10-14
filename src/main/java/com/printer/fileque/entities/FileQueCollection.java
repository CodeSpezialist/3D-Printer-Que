package com.printer.fileque.entities;

import org.springframework.stereotype.Component;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

@Component
public class FileQueCollection {

    private Queue<PrintFile> printQue = new LinkedList<>();

    public void addToPrintQue(PrintFile file) {
        printQue.add(file);
    }

    public PrintFile getNexFile() {
        return printQue.poll();
    }

    public boolean isQueEmpty() {
        return printQue.isEmpty();
    }

    public void setPrintQue(List<PrintFile> printFiles) {
        for (PrintFile singleItem : printFiles) {
            printQue = new LinkedList<>();
            printQue.add(singleItem);
        }
    }
}
