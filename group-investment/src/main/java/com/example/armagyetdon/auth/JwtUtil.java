package com.example.armagyetdon.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Set;

@Component
public class JwtUtil {
    private final SecretKey secretKey;

    @Autowired
    public JwtUtil(@Value("${spring.jwt.secret}") String secret){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String getLoginId(String token){
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload()
                .get("loginId", String.class);
    }

    public String getRole(String token){
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload()
                .get("role", String.class);
    }



    public Boolean isExpired(String token){
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload()
                .getExpiration().before(new Date());
    }

    public String createJwt(String loginId, String role, boolean teams, Long expiredMs){
        return Jwts.builder()
                .claim("username", loginId)
                .claim("role", role)
                .claim("teams", teams)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiredMs))
                .signWith(secretKey)
                .compact();
    }

}
