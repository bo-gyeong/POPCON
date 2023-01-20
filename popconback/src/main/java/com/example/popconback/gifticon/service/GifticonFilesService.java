package com.example.popconback.gifticon.service;

import com.example.popconback.gifticon.domain.GifticonFiles;

import com.example.popconback.gifticon.repository.GifticonFilesRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GifticonFilesService {
    @Autowired
    GifticonFilesRepository gifticonFilesRepository;

    public void save(GifticonFiles files) {
        GifticonFiles f = new GifticonFiles();
        f.setFileName(files.getFileName());

        gifticonFilesRepository.save(f);
    }
}
