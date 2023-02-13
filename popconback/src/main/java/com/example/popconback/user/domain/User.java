package com.example.popconback.user.domain;


import com.example.popconback.gifticon.domain.Favorites;
import com.example.popconback.gifticon.domain.Gifticon;
import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Getter
//@Setter
@Table(name = "user")
public class User {
    @Id
    private int hash;
    private String email;
    private String social;

    private String Token;

    private String refreshToken;

    private int alarm;
    private int Nday;
    private int term;
    private int timezone;
    private int manner_temp;

    @JsonBackReference//, orphanRemoval = true
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Gifticon> gifticonList;

    @JsonBackReference
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Favorites> favoriteskList;

    @Builder
    public User(int hash,
                String email,
                String social,
                String Token,
                String refreshtoken,
                int alarm,
                int Nday,
                int term,
                int timezone,
                int manner_temp) {
        this.hash = hash;
        this.email = email;
        this.social = social;
        this.Token =  Token;
        this.refreshToken = refreshtoken;
        this.alarm = alarm;
        this.Nday = Nday;
        this.term = term;
        this.timezone = timezone;
        this.manner_temp = manner_temp;
    }


    @Override
    public int hashCode() {
        return Objects.hash(email, social);
    }
}
