package com.example.popconback.gifticon.controller;

import com.example.popconback.gifticon.domain.Gifticon;
import com.example.popconback.gifticon.domain.GifticonFiles;
import com.example.popconback.gifticon.dto.GifticonFilesDto;
import com.example.popconback.gifticon.service.GifticonFilesService;
import com.example.popconback.gifticon.service.S3Service;
import lombok.AllArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
@Controller
@AllArgsConstructor
public class GifticonFilesController {

    private S3Service s3Service;

    private GifticonFilesService gifticonFilesService;


    @PostMapping("/mobile/upload.do")
    public ResponseEntity<Object> upload(GifticonFilesDto gifticonFilesDto, List<MultipartFile> multipartFiles) throws IOException, UncheckedIOException {
        List<String> imgPathList = new ArrayList<>();

        for (MultipartFile multipartFile : multipartFiles) {
            String imgPath = s3Service.upload(multipartFile);
            gifticonFilesDto.setFilePath(imgPath);
            imgPathList.add(imgPath);
            gifticonFilesService.savePost(gifticonFilesDto);


        }
        return new ResponseEntity<Object>(imgPathList, HttpStatus.OK);


    }


    @GetMapping("/gifticonfiles/{barcodeNum}")
    public ResponseEntity<List<GifticonFiles>> gifticonFilesList(@PathVariable String barcodeNum){
        return ResponseEntity.ok(gifticonFilesService.gifticonFilesList(barcodeNum));
    }

}
