package com.example.common.auth;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Component
public class JwtUtil {
    private final SecretKey secretKey;

    @Autowired
    public JwtUtil(@Value("${spring.jwt.secret}") String secret){
        this.secretKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public int getUserId(String token){
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload()
                .get("userId", Integer.class);
    }

    public int getTeamId(String token){
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload()
                .get("teamId", Integer.class);
    }

    public boolean isTeamExist(String token){
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload()
                .get("isTeamExist", Boolean.class);
    }

    public boolean validateToken(String token){
        try{
            Jwts.parser().verifyWith(secretKey).build().parseClaimsJws(token);
            return true;
        }catch (Exception e){
            return false;
        }
    }

    public Boolean isExpired(String token){
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(token).getPayload()
                .getExpiration().before(new Date());
    }

    public String createJwt(int loginId, Integer teamId, boolean isTeam){
        return Jwts.builder()
                .claim("userId", loginId)
                .claim("teamId", teamId)
                .claim("isTeamExist", isTeam)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis()+24*60*60*1000L))
                .signWith(secretKey)
                .compact();
    }


    public String extractToken(String authHeader) {
        return authHeader.substring(7);
    }

    public boolean containsTeam(String jwtToken) {
        return Jwts.parser().verifyWith(secretKey).build()
                .parseSignedClaims(jwtToken).getPayload()
                .containsKey("teamId");
    }
}
