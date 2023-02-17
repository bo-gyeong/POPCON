package com.example.popconback.gifticon.repository;


import com.example.popconback.gifticon.domain.Favorites;

import org.springframework.data.jpa.repository.JpaRepository;

import javax.transaction.Transactional;
import java.util.List;

public interface Favoritesrepository extends JpaRepository<Favorites, Long> {

    List<Favorites> findByUser_Hash(int hash);
    @Transactional
    void deleteByUser_HashAndBrand_BrandName(int hash, String brand_name);
}
