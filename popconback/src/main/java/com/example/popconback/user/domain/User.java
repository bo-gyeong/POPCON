package com.example.popconback.user.domain;


import com.example.popconback.gifticon.domain.Bookmark;
import com.example.popconback.gifticon.domain.Gifticon;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
@Getter
@Setter
@Table(name="user")
public class User {
    @Id
    private int hash;
    private String email;
    private String social;

    private String Token;

    private int alarm;
    private int Nday;
    private int term;
    private int timezone;
    private int manner_temp;

    @JsonBackReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Gifticon> gifticonList;

    @JsonBackReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Bookmark> bookmarkList;
    @Override
    public int hashCode() {
        return Objects.hash(email,social);
    }
}
