package com.example.popconback.gifticon.repository;

import com.example.popconback.gifticon.domain.Bookmark;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;

public interface Bookmarkrepository extends JpaRepository<Bookmark, Long> {
    @Transactional
    void deleteByUser_HashAndBrand_BrandName(int hash, String brand_name);
}
