package com.example.popconback.gifticon.dto;

import com.example.popconback.gifticon.domain.Gifticon;
import com.example.popconback.gifticon.domain.GifticonFiles;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class GifticonFilesDto {
    private Long id;
    private int imageType;

    private Gifticon gifticon;
    private String filePath;

    public GifticonFiles toEntity(){
        GifticonFiles build = GifticonFiles.builder()
                .id(id)
                .imageType(imageType)
                .gifticon(gifticon)
                .filePath(filePath)
                .build();
        return build;
    }

    @Builder
    public GifticonFilesDto(Long id, int imageType, Gifticon gifticon, String filePath) {
        this.id = id;
        this.imageType = imageType;
        this.gifticon = gifticon;
        this.filePath = filePath;
    }
}
