package com.ecommerce.Gateway.JWTUtil;


import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;

@Component
public class JwtUtil {

    private static final String SECRET =
            "Choose a secret key.Choose a secret key.Choose a secret key.Choose a secret key.Choose a secret key.Choose a secret key";

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String createToken(String username,String role,Integer userId) {

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .claim("userId",userId)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 10080 * 60 * 1000)
                )
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }


    public boolean validateToken(String token) {
        try {
            Claims claims = extractAllClaims(token);
            return !claims.getExpiration().before(new Date());
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }



    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }


}

