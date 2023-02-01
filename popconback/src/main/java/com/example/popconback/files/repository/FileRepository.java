package com.example.popconback.files.repository;

import com.example.popconback.files.domain.InputFile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FileRepository extends JpaRepository<InputFile, Long> {

    InputFile findByFileName(String fileName);

    List<InputFile> findByGifticon_BarcodeNum(String barcodeNum);
}