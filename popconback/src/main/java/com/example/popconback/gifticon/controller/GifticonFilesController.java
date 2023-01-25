package com.example.popconback.gifticon.controller;

import com.example.popconback.gifticon.domain.Gifticon;
import com.example.popconback.gifticon.domain.GifticonFiles;
import com.example.popconback.gifticon.dto.GifticonFilesDto;
import com.example.popconback.gifticon.service.GifticonFilesService;
import com.example.popconback.gifticon.service.S3Service;
import io.swagger.annotations.*;
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

@Api(value = "GifticonFilesContoller")
@SwaggerDefinition(tags = {@Tag(name = "GifticonFilesContoller",
        description = "기프티콘 파일 컨트롤러")})
@Controller
@RequestMapping(value = "/api/v1/gifticons/files/")
@AllArgsConstructor
public class GifticonFilesController {

    private S3Service s3Service;

    private GifticonFilesService gifticonFilesService;


    @ApiOperation(value = "기프티콘 이미지 업로드", notes = "기프티콘 이미지를 서버에 업로드하여 저장", httpMethod = "POST")
    @PostMapping("/upload")
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

    @ApiOperation(value = "기프티콘 이미지 찾기", notes = "바코드 넘버로 기프티콘 이미지 찾기", httpMethod = "GET")
    @ApiImplicitParam(
            name = "barcodeNum",
            value = "바코드 넘버",
            required = true,
            dataType = "string",
            paramType = "path",
            defaultValue = "None"
    )
    @GetMapping("/{barcodeNum}")
    public ResponseEntity<List<GifticonFiles>> gifticonFilesList(@PathVariable String barcodeNum){
        return ResponseEntity.ok(gifticonFilesService.gifticonFilesList(barcodeNum));
    }

}
