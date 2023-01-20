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
    private String fileName;

    private Gifticon gifticon;
    private String filePath;

    public GifticonFiles toEntity(){
        GifticonFiles build = GifticonFiles.builder()
                .id(id)
                .fileName(fileName)
                .gifticon(gifticon)
                .filePath(filePath)
                .build();
        return build;
    }

    @Builder
    public GifticonFilesDto(Long id,String fileName, Gifticon gifticon, String filePath) {
        this.id = id;
        this.fileName = fileName;
        this.gifticon = gifticon;
        this.filePath = filePath;
    }
}
