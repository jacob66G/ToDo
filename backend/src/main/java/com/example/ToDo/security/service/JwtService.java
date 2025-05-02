package com.example.ToDo.security.service;

import com.example.ToDo.constant.ApplicationConstants;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {

    @Value("${security.jwt.secret-key}")
    private String secretKey;

    @Value("${security.jwt.expiration-time}")
    private long jwtExpiration;

    public String generateToken(Authentication authentication) {
        return Jwts
                .builder()
                .issuer(ApplicationConstants.APPLICATION_NAME)
                .subject(authentication.getName())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + jwtExpiration))
                .signWith(getSignInKey())
                .compact();
    }

    public Key getSignInKey(){
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Claims validateToken(String token) throws JwtException {
        return Jwts
                .parser()
                .verifyWith((SecretKey)getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}
