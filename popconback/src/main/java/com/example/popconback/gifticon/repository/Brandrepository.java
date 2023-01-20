package com.example.popconback.gifticon.repository;

import com.example.popconback.gifticon.domain.Brand;

import com.example.popconback.gifticon.domain.GifticonFiles;
import org.springframework.data.jpa.repository.JpaRepository;

public interface Brandrepository extends JpaRepository<Brand, String> {

    Brand findByBrandName(String brandName);
}
