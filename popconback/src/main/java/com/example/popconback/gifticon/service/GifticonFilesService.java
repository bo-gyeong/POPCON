package com.example.popconback.gifticon.service;

import com.example.popconback.gifticon.domain.Gifticon;
import com.example.popconback.gifticon.domain.GifticonFiles;

import com.example.popconback.gifticon.dto.GifticonFilesDto;
import com.example.popconback.gifticon.repository.GifticonFilesRepository;
import com.example.popconback.user.domain.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

import static org.springframework.data.domain.Sort.Order.asc;

@Service
@AllArgsConstructor
public class GifticonFilesService {
    @Autowired
    GifticonFilesRepository gifticonFilesRepository;

    public void savePost(GifticonFilesDto gifticonFilesDto) {
        gifticonFilesRepository.save(gifticonFilesDto.toEntity());

    }


    public List<GifticonFiles> gifticonFilesList (String barcodeNum){

        return gifticonFilesRepository.findByGifticon_BarcodeNum(barcodeNum);
    }



}
