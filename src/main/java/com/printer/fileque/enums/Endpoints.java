package com.printer.fileque.enums;

import lombok.Getter;

@Getter
public enum Endpoints {

    FILE_UPLOAD("/files/local"),
    JOB_STATUS("/job"),
    PRINTER_COMMAND("/printer/command"),
    CONNECTION_STATUS("/connection");

    private final String url;

    Endpoints(String url) {
        this.url = url;
    }
}
