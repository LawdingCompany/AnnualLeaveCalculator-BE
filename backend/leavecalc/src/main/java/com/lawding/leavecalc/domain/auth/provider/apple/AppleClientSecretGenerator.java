package com.lawding.leavecalc.domain.auth.provider.apple;

import com.lawding.leavecalc.domain.global.config.properties.AppleProperties;
import io.jsonwebtoken.Jwts;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppleClientSecretGenerator {

    private final AppleProperties appleProperties;

    public String generate() {
        try {
            PrivateKey privateKey = getPrivateKey();

            return Jwts.builder()
                .header().add("kid", appleProperties.getKeyId()).and()
                .issuer(appleProperties.getTeamId())
                .issuedAt(Date.from(Instant.now()))
                .expiration(Date.from(Instant.now().plusSeconds(300))) // 5분
                .audience().add("https://appleid.apple.com").and()
                .subject(appleProperties.getClientId())
                .signWith(privateKey, Jwts.SIG.ES256)
                .compact();
        } catch (Exception e) {
            throw new RuntimeException("Apple client secret 생성 실패", e);
        }
    }

    private PrivateKey getPrivateKey() throws Exception {
        String privateKeyStr = appleProperties.getPrivateKey()
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s+", "");

        byte[] keyBytes = Base64.getDecoder().decode(privateKeyStr);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory keyFactory = KeyFactory.getInstance("EC");
        return keyFactory.generatePrivate(keySpec);
    }
}
