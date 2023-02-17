package com.example.popconback.utils;

import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;


public class JwtUtil {

    public static String getUserName(String token, String secretKey) {
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("UserName",String.class);
    }

    public static String getSocial(String token, String secretKey){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().get("Social",String.class);
    }

    public static boolean isExpired(String token, String secretKey){
        return Jwts.parser().setSigningKey(secretKey).parseClaimsJws(token)
                .getBody().getExpiration().before(new Date());
    }
    public static String creatJwt(String userName,String social, String secretKey, Long expiredMs){
        Claims claims = Jwts.claims();
        claims.put("UserName", userName);
        claims.put("Social", social);

        return Jwts.builder()
                .setHeaderParam("type","jwt")
                .setClaims(claims)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expiredMs))
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();
    }
    public static String creatRefashToken( Long expiredMs,String secretKey){

        return Jwts.builder()
                .setHeaderParam("type","Refresh")
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis()+expiredMs))
                .signWith(SignatureAlgorithm.HS256,secretKey)
                .compact();
    }


}
