package com.example.popconback.files.domain;

import com.example.popconback.gifticon.domain.Gifticon;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
<<<<<<< HEAD
@Setter
=======
>>>>>>> cd324043697fc12e1cadfebcafe784654184a4d8
@Table(name="files")
public class InputFile {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private Long id;

    @Column(nullable = true)
    private Integer imageType;

    @ManyToOne
    @JsonBackReference
    @JoinColumn(name="barcode_num")
    private Gifticon gifticon;

    private String fileName;
    @Column(columnDefinition = "TEXT")
    private String filePath;




    @Builder
    public InputFile(Integer imageType, Gifticon gifticon, String fileName, String filePath) {

        this.imageType = imageType;
        this.gifticon = gifticon;
        this.fileName = fileName;
        this.filePath = filePath;

    }

}