package com.example.mutsamarket.jwt;

import io.jsonwebtoken.*;
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
    private final JwtParser jwtParser;
    public JwtTokenUtils(
            @Value("${jwt.secret}") String jwtSecret
    ) {
        this.signingKey = Keys.hmacShaKeyFor(jwtSecret.getBytes()); //암호화
        this.jwtParser = Jwts.parserBuilder().setSigningKey(this.signingKey).build();
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

    //jwt를 해석해서 유효한 jwt인지 판단
    public boolean validate(String token){
        try{
            jwtParser.parseClaimsJws(token);
            return true;
        } catch (SecurityException | MalformedJwtException e){
            log.warn("malformed jwt");
        } catch (ExpiredJwtException e) {
            log.warn("expired jwt");
        } catch (UnsupportedJwtException e) {
            log.warn("unsupported jwt");
        } catch (IllegalArgumentException e) {
            log.warn("illegal argument");
        }
        return false;
    }

    //JWT를 해석해서 Claims부분만 반환
    public Claims parseClaims(String token){
        return jwtParser.parseClaimsJws(token).getBody();
    }
}
