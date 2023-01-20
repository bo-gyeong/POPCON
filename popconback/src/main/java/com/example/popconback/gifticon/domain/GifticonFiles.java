package com.example.popconback.gifticon.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
@Table(name="gifticon_files")
public class GifticonFiles {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(nullable = true)
    private int imageType;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="barcode_num")
    private Gifticon gifticon;

    @Column(columnDefinition = "TEXT")
    private String filePath;


    @Builder
    public GifticonFiles(Long id, int imageType, Gifticon gifticon, String filePath) {
        this.id = id;
        this.imageType = imageType;
        this.gifticon = gifticon;
        this.filePath = filePath;
    }

}
