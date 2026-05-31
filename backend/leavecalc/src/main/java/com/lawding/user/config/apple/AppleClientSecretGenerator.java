package com.lawding.user.config.apple;

import io.jsonwebtoken.Jwts;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Date;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class AppleClientSecretGenerator {

    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String appleClientId;

    @Value("${app.apple.team-id}")
    private String appleTeamId;

    @Value("${app.apple.key-id}")
    private String appleKeyId;

    @Value("${app.apple.private-key}")
    private String applePrivateKey;

    public String createAppleClientSecret() {
        try {
            Date expirationDate = Date.from(Instant.now().plus(30, ChronoUnit.DAYS));

            return Jwts.builder()
                .header().keyId(appleKeyId).add("alg", "ES256").and()
                .issuer(appleTeamId)
                .issuedAt(new Date())
                .expiration(expirationDate)
                .audience().add("https://appleid.apple.com").and()
                .subject(appleClientId)
                .signWith(getPrivateKey(), Jwts.SIG.ES256)
                .compact();
        } catch (Exception e) {
            throw new RuntimeException("애플 Secret 생성 실패", e);
        }
    }

    private PrivateKey getPrivateKey() throws Exception {
        String privateKeyPEM = applePrivateKey
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s+", "");

        byte[] decodedKey = Base64.getDecoder().decode(privateKeyPEM);
        PKCS8EncodedKeySpec spec = new PKCS8EncodedKeySpec(decodedKey);
        KeyFactory kf = KeyFactory.getInstance("EC");
        return kf.generatePrivate(spec);
    }
}
