package com.lawding.auth.jwt;

import com.lawding.global.exception.ClientException;
import com.lawding.global.exception.ErrorCode;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JwtProvider {

    private final SecretKey secretKey;
    private final long accessTokenExpirationMs;
    private final long refreshTokenExpirationMs;

    public JwtProvider(
        @Value("${app.jwt.secret}") String secret,
        @Value("${app.jwt.access-expiration}") long accessTokenExpirationMs,
        @Value("${app.jwt.refresh-expiration}") long refreshTokenExpirationMs
    ) {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    private String createToken(Long userId, long expirationMs) {
        Date now = new Date();
        return Jwts.builder()
            .subject(String.valueOf(userId))
            .issuedAt(now)
            .expiration(new Date(now.getTime() + expirationMs))
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact();
    }

    public String createAccessToken(Long userId) {
        return createToken(userId, accessTokenExpirationMs);
    }

    public String createRefreshToken(Long userId) {
        return createToken(userId, refreshTokenExpirationMs);
    }

    public boolean validateToken(String token) {
        parseClaims(token);
        return true;
    }

    public Long getUserIdFromToken(String token) {
        return Long.parseLong(parseClaims(token).getSubject());
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
        } catch (ExpiredJwtException e) {
            throw new ClientException(ErrorCode.TOKEN_EXPIRED);
        } catch (JwtException | IllegalArgumentException e) {
            throw new ClientException(ErrorCode.INVALID_TOKEN);
        }
    }
}
