package com.lawding.auth.jwt;

import io.jsonwebtoken.Claims;
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
        @Value("${app.jwt.secret}")
        String secret,
        @Value("${app.jwt.access-expiration}")
        long accessTokenExpirationMs,
        @Value("${app.jwt.refresh-expiration}")
        long refreshTokenExpirationMs) {
        // Base64로 인코딩된 키를 디코딩해서 안전한 SecretKey 객체로 만듭니다.
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessTokenExpirationMs = accessTokenExpirationMs;
        this.refreshTokenExpirationMs = refreshTokenExpirationMs;
    }

    // 1. JWT Access Token 생성기
    // 토큰 안에 식별자인 유저 ID(PK)를 넣어둡니다.
    public String createAccessToken(Long userId, String email) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessTokenExpirationMs);

        return Jwts.builder()
            .subject(String.valueOf(userId)) // Token의 주인을 userId로 설정!
            .claim("email", email)           // 추가 데이터(Claim)로 이메일도 살짝 넣어줌
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256) // 우리만의 비밀키로 서명!
            .compact();
    }

    // 2. JWT Refresh Token 생성기 (새로 추가!)
    // RT는 보통 추가 데이터(Claim) 없이 유저 ID 정도만 최소한으로 넣어둡니다.
    public String createRefreshToken(Long userId) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshTokenExpirationMs);

        return Jwts.builder()
            .subject(String.valueOf(userId))
            .issuedAt(now)
            .expiration(expiryDate)
            .signWith(secretKey, Jwts.SIG.HS256)
            .compact();
    }

    // 3. JWT 토큰 유효성 검증기 (기존과 동일 - AT, RT 둘 다 이 메서드로 검증 가능!)
    public boolean validateToken(String token) {
        try {
            Jwts.parser()
                .verifyWith(secretKey) // 이 키로 서명된 게 맞는지 확인!
                .build()
                .parseSignedClaims(token);
            return true; // 에러가 안 나면 정상 토큰!
        } catch (JwtException | IllegalArgumentException e) {
            // 토큰이 조작되었거나, 만료되었거나, 비어있을 경우 모두 여기로 빠집니다.
            System.out.println("유효하지 않은 JWT 토큰입니다: " + e.getMessage());
            return false;
        }
    }

    // 4. 토큰에서 유저 ID 꺼내기
    // 검증이 끝난 토큰을 분해해서 안에 들어있는 userId를 꺼냅니다.
    public Long getUserIdFromToken(String token) {
        Claims claims = Jwts.parser()
            .verifyWith(secretKey)
            .build()
            .parseSignedClaims(token)
            .getPayload(); // 0.12.x 최신 문법 (getBody() 대신 getPayload())

        return Long.parseLong(claims.getSubject());
    }
}
