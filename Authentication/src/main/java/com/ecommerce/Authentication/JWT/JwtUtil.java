package com.ecommerce.Authentication.JWT;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.List;

@Component
public class JwtUtil {

    private static final String SECRET =
            "Choose a secret key.Choose a secret key.Choose a secret key.Choose a secret key.Choose a secret key.Choose a secret key";

    private final Key key = Keys.hmacShaKeyFor(SECRET.getBytes());

    public String createToken(String username,String role) {

        return Jwts.builder()
                .setSubject(username)
                .claim("role", role)
                .setIssuedAt(new Date())
                .setExpiration(
                        new Date(System.currentTimeMillis() + 10080 * 60 * 1000)
                )
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }



//    public Claims extractUsernameAndRole(String token) {
//
//        return Jwts.parserBuilder()
//                .setSigningKey(key)
//                .build()
//                .parseClaimsJws(token)
//                .getBody();
//
//
//    }


}
