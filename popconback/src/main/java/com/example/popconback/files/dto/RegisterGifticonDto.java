package com.example.popconback.files.dto;

import lombok.*;

import org.springframework.web.multipart.MultipartFile;

@Data
@NoArgsConstructor
@Getter
@Setter
public class RegisterGifticonDto {
    // 이미지 두장, 바코드 넘버, 원본 파일 이름


    String barcodeNum;

    String originGcpFileName;

    String barcodeGcpFileName;
    String productGcpFileName;

    @Builder
    public RegisterGifticonDto(String barcodeNum, String originGcpFileName, String barcodeGcpFileName, String productGcpFileName) {
        this.barcodeNum = barcodeNum;
        this.originGcpFileName = originGcpFileName;
        this.barcodeGcpFileName = barcodeGcpFileName;
        this.productGcpFileName = productGcpFileName;
    }




}

