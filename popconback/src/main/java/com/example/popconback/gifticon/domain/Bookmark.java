package com.example.popconback.gifticon.domain;


import com.example.popconback.user.domain.User;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name="bookmark")
public class Bookmark {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name="hash")
    @JsonManagedReference
    private User user;

    @ManyToOne
    @JoinColumn(name="brand_name")
    @JsonManagedReference
    private Brand brand;

}
