package com.lawding.leavecalc.domain.auth.provider.apple;

import com.lawding.leavecalc.domain.auth.client.apple.AppleAuthClient;
import com.lawding.leavecalc.domain.auth.client.apple.AppleTokenResponse;
import com.lawding.leavecalc.domain.global.config.properties.AppleProperties;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import java.math.BigInteger;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.RSAPublicKeySpec;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AppleAuthProvider {
    private final AppleProperties appleProperties;
    private final AppleAuthClient appleAuthClient;
    private final AppleClientSecretGenerator clientSecretGenerator;

    public String getAppleUserId(String authorizationCode) {
        String clientSecret = clientSecretGenerator.generate();

        AppleTokenResponse tokenResponse = appleAuthClient.getToken(
            authorizationCode,
            clientSecret,
            appleProperties.getClientId(),
            appleProperties.getRedirectUri()
        );

        return extractSubFromIdToken(tokenResponse.idToken());
    }

    private String extractSubFromIdToken(String idToken) {
        // Apple 공개키 가져오기
        List<Map<String, String>> keys = appleAuthClient.getPublicKeys();

        for (Map<String, String> key : keys) {
            try {
                PublicKey publicKey = buildPublicKey(key);

                Claims claims = Jwts.parser()
                    .verifyWith(publicKey)
                    .build()
                    .parseSignedClaims(idToken)
                    .getPayload();

                return claims.getSubject(); // 유저 고유 ID
            } catch (Exception ignored) {}
        }

        throw new RuntimeException("Apple id_token 검증 실패");
    }

    private PublicKey buildPublicKey(Map<String, String> key) throws Exception {
        byte[] nBytes = Base64.getUrlDecoder().decode(key.get("n"));
        byte[] eBytes = Base64.getUrlDecoder().decode(key.get("e"));

        RSAPublicKeySpec spec = new RSAPublicKeySpec(
            new BigInteger(1, nBytes),
            new BigInteger(1, eBytes)
        );

        return KeyFactory.getInstance("RSA").generatePublic(spec);
    }
}
