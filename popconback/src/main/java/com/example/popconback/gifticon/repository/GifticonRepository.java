package com.example.popconback.gifticon.repository;

import com.example.popconback.gifticon.domain.Brand;
import com.example.popconback.gifticon.domain.Gifticon;
import com.example.popconback.gifticon.dto.SortBrand.SordBrandDto;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.Date;
import java.util.List;
import java.util.Map;

public interface GifticonRepository extends JpaRepository<Gifticon, String> {
    List<Gifticon> findByUser_HashAndState(int hash, int state, Sort sort);
    List<Gifticon> findByUser_HashAndStateAndIsVoucher(int hash, int state,int isv, Sort sort);
    List<Gifticon> findByUser_HashAndBrand_BrandNameAndState(int hash, String brand_name,int state, Sort sort);
    List<Gifticon>  findByUser_HashAndDueLessThanAndState(int hash, Date date, int state);
    List<Gifticon> findByDueLessThanEqualAndState(Date date, int state);

    List<Gifticon> findByUser_HashAndStateBetween(int hash, int state, int end);

    Gifticon findByBarcodeNum(String barcodeNum);
    @Query(value = "SELECT g.brand_name, b.brand_img, COUNT(g.brand_name) as cnt FROM gifticon g JOIN brand b ON g.brand_name = b.brand_name WHERE hash = :hash AND state = 0 GROUP BY g.brand_name ORDER BY cnt desc", nativeQuery = true)
    List<Map<String,Object>> selectSQLById(@Param(value = "hash") int hash);
}
