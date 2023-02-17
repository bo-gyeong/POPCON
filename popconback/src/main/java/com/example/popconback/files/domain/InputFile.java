package com.example.popconback.files.domain;

import com.example.popconback.gifticon.domain.Gifticon;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Entity
@Getter
@Setter
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

    private int width;
    private int height;




    @Builder
    public InputFile(Integer imageType, Gifticon gifticon, String fileName, String filePath, int width, int height) {

        this.imageType = imageType;
        this.gifticon = gifticon;
        this.fileName = fileName;
        this.filePath = filePath;
        this.width = width;
        this.height = height;

    }

}