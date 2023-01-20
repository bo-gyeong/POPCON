package com.example.popconback.gifticon.repository;

import com.example.popconback.gifticon.domain.GifticonFiles;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GifticonFilesRepository extends JpaRepository<GifticonFiles, String> {

    List<GifticonFiles> findByGifticon_BarcodeNum(String barcodeNum);

}
