package com.example.popconback.files.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class RegisterGifticonDto {
    // 이미지 두장, 바코드 넘버, 원본 파일 이름

    MultipartFile[] files;

    String barcodeNum;

    String originFileName;


}