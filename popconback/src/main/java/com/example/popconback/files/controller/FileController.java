package com.example.popconback.files.controller;

import com.example.popconback.files.domain.InputFile;
import com.example.popconback.files.dto.RegisterGifticonDto;
import com.example.popconback.files.service.FileService;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    private final FileService fileService;

    @ApiOperation(value = "addOriginImageFile",
            notes = "기프티콘 등록위해 원본 이미지 리스트 업로드",
            httpMethod = "POST")
    @PostMapping(value = "/add_origin",consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public List<InputFile> addFile(@RequestParam("file") MultipartFile[] files){
        LOGGER.debug("Call addFile API");
        return fileService.uploadFiles(files);
    }

    // file올리면 gcp파일이름+url주고 -> dto (세장 gcp파일 이름 & 바코드) 받은다음 이미지타입 및 바코드 db 저장
    @ApiOperation(value = "registerGifticon",
            notes = "등록하기 버튼 누른 후 상품,바코드 이미지 저장 및 db 업데이트",
            httpMethod = "POST")
    @PostMapping(value="/register_gifticon")
    public List<List<InputFile>> registerGifticon(@RequestBody RegisterGifticonDto[] registerGifticonDtoList){
        LOGGER.debug("Call registerGifticon API");

        List<List<InputFile>> allResultList = new ArrayList<>();

        for (RegisterGifticonDto registerGifticonDto : registerGifticonDtoList) {
            allResultList.add(fileService.registerGifticon(registerGifticonDto));
            }




        return allResultList;
    }
}
