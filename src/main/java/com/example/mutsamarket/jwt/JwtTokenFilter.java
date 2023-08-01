package com.example.mutsamarket.jwt;

import com.example.mutsamarket.entity.CustomUserDetails;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.ArrayList;

@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {
    private JwtTokenUtils jWtTokenUtils;

    public JwtTokenFilter(JwtTokenUtils jWtTokenUtils) {
        this.jWtTokenUtils = jWtTokenUtils;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, HttpServletResponse response, FilterChain filterChain
    ) throws ServletException, IOException {
         String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);
         if(authHeader != null && authHeader.startsWith("Bearer ")){

             if (authHeader.length() >= 8){ //"Bearer "뒤에 글자가 더 있는지 판단
                 String jwt = authHeader.split(" ")[1];
                 if (jWtTokenUtils.validate(jwt)){

                     SecurityContext context = SecurityContextHolder.createEmptyContext();
                     String username = jWtTokenUtils.parseClaims(jwt).getSubject();
                     //TODO 권한 관련해서 설정
                     //사용자 인증정보 설정
                     AbstractAuthenticationToken authenticationToken
                             = new UsernamePasswordAuthenticationToken(
                                     CustomUserDetails.builder().username(username).build(),
                                    jwt, new ArrayList<>()
                            );
                     context.setAuthentication(authenticationToken);
                     SecurityContextHolder.setContext(context);
                     log.info("set security context with jwt");

                 } else {
                     log.warn("jwt validation failed");
                 }
             }
             else {
                 log.warn("jwt validation failed");
             }

         }

         filterChain.doFilter(request, response);
    }
}
