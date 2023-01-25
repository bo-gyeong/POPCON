package com.example.popconback.gifticon.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name="brand")
public class Brand {
    @Id
    private String brandName;

    private String brandImg;

    @JsonBackReference
    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Gifticon> gifticonList = new ArrayList<>();

    @JsonBackReference
    @OneToMany(mappedBy = "brand", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Favorites> favoritesList = new ArrayList<>();
}
