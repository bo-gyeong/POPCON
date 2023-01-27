package com.example.popconback.files.service;

import com.example.popconback.files.domain.InputFile;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface FileService {

    List<InputFile> uploadFiles(MultipartFile[] files);
}