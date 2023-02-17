package com.example.popconback.user.domain;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable
public class UserId implements Serializable {
    @Column(name="email")
    private String email;
    @Column(name="social")
    private String social;

    public UserId(){

    }

    public UserId(String email, String social){
        this.email = email;
        this.social = social;
    }

    @Override
    public boolean equals(Object obj){
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        UserId userId = (UserId) obj;
        return email.equals(userId.email) && social.equals(userId.social);
    }

    @Override
    public int hashCode() {
        return Objects.hash(email,social);
    }


}
