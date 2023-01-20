package com.example.popconback.gifticon.repository;

import com.example.popconback.gifticon.domain.GifticonFiles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GifticonFilesRepository extends JpaRepository<GifticonFiles, String> {

    GifticonFiles findByFileName(String fileName);
}
