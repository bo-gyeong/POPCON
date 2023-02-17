package com.example.popconback.files.exception;

import lombok.Data;
import java.time.ZonedDateTime;

@Data
public class ApiException {

    private String errorMessage;
    private Integer statusCode;
    private ZonedDateTime zonedDateTime;
}