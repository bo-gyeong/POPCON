package com.example.popconback.gifticon.repository;

import com.example.popconback.gifticon.domain.Gifticon;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GifticonRepository extends JpaRepository<Gifticon, String> {
    List<Gifticon> findByUser_Hash(int hash, Sort sort);
    List<Gifticon> findByUser_HashAndBrand_BrandName(int hash, String brand_name);
}
