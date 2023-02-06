package com.example.popconback.gifticon.repository;

import com.example.popconback.gifticon.domain.Present;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PresentRepository extends JpaRepository<Present, Long> {

    List<Present> findByXAndY(String x, String y);
}
