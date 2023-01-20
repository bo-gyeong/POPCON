package com.example.popconback.gifticon.controller;

import com.example.popconback.gifticon.domain.GifticonFiles;
import com.example.popconback.gifticon.service.GifticonFilesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;


import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@RestController
public class GifticonFilesController {
    GifticonFiles gifticonFile = new GifticonFiles();

    @Autowired
    GifticonFilesService gifticonFilesService;
    @PostMapping("/mobile/upload.do")
    public String upload(@RequestParam("multipartFiles") List<MultipartFile> multipartFiles) throws Exception {
        System.out.println("multipartFiles.size():"+multipartFiles.size());

        for (MultipartFile multipartFile : multipartFiles) {
            BufferedImage image = ImageIO.read(multipartFile.getInputStream());

            String[] array = multipartFile.getContentType().split("/");
            for(String data: array) {
                System.out.println("array : "+data);
            }
            String filename = multipartFile.getOriginalFilename();

            gifticonFile.setFileName(filename);
            gifticonFilesService.save(gifticonFile);

            String temp_path = "C:/upload"; //폴더 경로
            File Folder = new File(temp_path);

            // 해당 디렉토리가 없다면 디렉토리를 생성.
            if (!Folder.exists()) {
                try{
                    Folder.mkdir(); //폴더 생성합니다. ("새폴더"만 생성)
                    System.out.println("폴더가 생성완료.");
                }
                catch(Exception e){
                    e.getStackTrace();
                }
            }else {
                System.out.println("폴더가 이미 존재합니다..");
            }



            System.out.println("filename:"+filename);
            String path = "C:/upload/" + filename;
            File outputFile = new File(path);
            ImageIO.write(image,"jpg",outputFile);
        }
        return "success";
    }
    @GetMapping(value="/mobile/download.do")
    public ResponseEntity<ByteArrayResource> download(@RequestParam("filename") String filename) throws IOException {
        System.out.println("download:"+filename);

        Path path = Paths.get("C:/upload/"+filename);
        byte[] data = Files.readAllBytes(path);
        ByteArrayResource resource = new ByteArrayResource(data);

        return ResponseEntity.ok()
                // Content-Disposition
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + path.getFileName().toString())
                // Content-Type
                .contentType(MediaType.parseMediaType("upload/jpeg")) //
                // Content-Lengh
                .contentLength(data.length) //
                .body(resource);
    }

}
