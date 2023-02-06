package com.example.popconback.gifticon.repository;

import com.example.popconback.gifticon.domain.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface Brandrepository extends JpaRepository<Brand, String> {

    Brand findByBrandName(String brandName);

    List<Brand> findAllByOrderByCountOfGifticonsDesc();
}
