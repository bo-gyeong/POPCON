package com.example.popconback.gifticon.domain;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="gifticon_files")
public class GifticonFiles {

    @Id
    private String fileName;

    @ManyToOne
    @JoinColumn(name="gifticon_barcode")
    @JsonManagedReference
    private Gifticon gifticon;


}
