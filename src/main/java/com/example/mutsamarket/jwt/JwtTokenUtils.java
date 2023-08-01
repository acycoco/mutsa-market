package com.example.mutsamarket.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;


@Slf4j
@Component
public class JwtTokenUtils {

    private final Key signingKey;
    public JwtTokenUtils(
            @Value("${jwt.secret}") String jwtSecret
    ) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes()); //암호화
    }

    //JWT 생성
    public String generateToken(UserDetails userDetails){
        Claims jwtClaims = Jwts.claims()
                .setSubject(userDetails.getUsername()) //username
                .setIssuedAt(Date.from(Instant.now())) //발급시간
                .setExpiration(Date.from(Instant.now().plusSeconds(36000))); //1시간 동안 유효

        return Jwts.builder()
                .setClaims(jwtClaims)
                .signWith(signingKey)
                .compact();
    }

}
