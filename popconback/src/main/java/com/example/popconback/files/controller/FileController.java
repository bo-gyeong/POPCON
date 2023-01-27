package com.example.popconback.files.controller;

import com.example.popconback.files.domain.InputFile;
import com.example.popconback.files.service.FileService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileController.class);

    private final FileService fileService;

    @PostMapping(consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public List<InputFile> addFile(@RequestBody MultipartFile[] files){
        LOGGER.debug("Call addFile API");
        return fileService.uploadFiles(files);
    }
}
