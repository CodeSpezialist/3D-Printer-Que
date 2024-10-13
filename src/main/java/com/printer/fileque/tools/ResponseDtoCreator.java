package com.printer.fileque.tools;

import com.printer.fileque.dtos.ResponseDto;

public class ResponseDtoCreator {

    public static <T> ResponseDto<T> createResponseDto(T data) {
        return ResponseDto.<T>builder()
                .success(true)
                .message("")
                .data(data)
                .build();
    }
}
