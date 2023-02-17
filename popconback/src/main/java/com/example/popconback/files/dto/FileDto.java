package com.example.popconback.files.dto;
import com.example.popconback.gifticon.domain.Gifticon;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class FileDto {


    private Integer imageType;


    private Gifticon gifticon;

    private String fileName;
    private String filePath;



}