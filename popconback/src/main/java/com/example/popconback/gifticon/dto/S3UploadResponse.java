package com.example.popconback.gifticon.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;


@Getter
@Setter
@ToString
public class S3UploadResponse {
    private String fileName;

    private String filePath;

    public S3UploadResponse() {}

    public S3UploadResponse(String fileName, String filePath) {
        this.fileName = fileName;
        this.filePath = filePath;
    }
}
