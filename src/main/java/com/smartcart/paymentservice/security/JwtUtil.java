package com.smartcart.paymentservice.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;

public class JwtUtil {
    private String secretKey;

    public JwtUtil(String jwtSecret) {
        this.secretKey = jwtSecret;
    }

    public SecretKey getKey(){
        return Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Validates token:
     * - signature
     * - expiration
     * - structure
     * Throws exception if invalid
     */

    public Claims validateToken(String token){
        Claims claims= Jwts.parser()
                .setSigningKey(getKey())
                .parseClaimsJws(token)
                .getBody();
        return claims;
    }
}
