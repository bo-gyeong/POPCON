package com.example.popconback.gifticon.repository;

import com.example.popconback.gifticon.domain.Gifticon;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;


import java.util.Date;
import java.util.List;

public interface GifticonRepository extends JpaRepository<Gifticon, String> {
    List<Gifticon> findByUser_Hash(int hash, Sort sort);
    List<Gifticon> findByUser_HashAndBrand_BrandName(int hash, String brand_name);
    List<Gifticon>  findByUser_HashAndDueLessThanEqualAndState(int hash, Date date, int state);
    List<Gifticon> findByDueAndState(Date date, int state);

    List<Gifticon> findByUser_HashAndStateGreaterThanEqual(int hash, int state);

    Gifticon findByBarcodeNum(String barcodeNum);
}
