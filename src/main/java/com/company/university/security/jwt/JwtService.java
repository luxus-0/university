package com.company.university.security.jwt;

import com.company.university.security.config.JwtConfigurationProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.Set;

@Service
public class JwtService {

    private final JwtConfigurationProperties properties;

    public JwtService(JwtConfigurationProperties properties) {
        this.properties = properties;
    }


    public String generateToken(String username, Set<String> roles) {
        Instant now = Instant.now();
        Instant expiration = now.plusMillis(properties.getExpirationMs());

        return Jwts.builder()
                .subject(username)
                .claim("roles", String.join(",", roles))
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiration))
                .issuer(properties.getIssuer())
                .signWith(Jwts.KEY.A128KW.key().build())
                .compact();
    }

    private Jws<Claims> parseToken(String token) {
        return Jwts
                .parser()
                .verifyWith(Jwts.KEY.A128KW.key().build())
                .build()
                .parseSignedClaims(token);
        }

    public String extractUsername(String token) {
        return parseToken(token).getPayload().getSubject();
    }

    public boolean isTokenValid(String token, String username) {
        String tokenUsername = extractUsername(token);
        return username.equals(tokenUsername) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        Date expiration = parseToken(token).getPayload().getExpiration();
        return expiration.before(new Date());
    }

    public Claims extractAllClaims(String token) {
        return parseToken(token).getPayload();
    }
}