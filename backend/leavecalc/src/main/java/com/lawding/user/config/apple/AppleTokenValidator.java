package com.lawding.user.config.apple;

import com.nimbusds.jose.JWSVerifier;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jose.jwk.JWK;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import com.lawding.user.client.ApplePublicKeyClient;
import java.util.Date;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class AppleTokenValidator {

    private final ApplePublicKeyClient applePublicKeyClient;

    @Value("${spring.security.oauth2.client.registration.apple.client-id}")
    private String appleClientId;

    public Map<String, Object> validateAndParseIdToken(String idToken) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(idToken);
            String keyId = signedJWT.getHeader().getKeyID();

            // 1. 애플 공개키 목록 가져오기
            JWKSet jwkSet = applePublicKeyClient.getApplePublicKeys();
            JWK jwk = jwkSet.getKeyByKeyId(keyId);

            if (jwk == null) {
                throw new RuntimeException("매칭되는 애플 공개키 없음: kid=" + keyId);
            }

            // ✨ 2. 서명 검증 (문제의 원인이었던 코드를 완벽하게 고쳤습니다!)
            JWSVerifier verifier = new RSASSAVerifier(jwk.toRSAKey());

            if (!signedJWT.verify(verifier)) {
                throw new RuntimeException("애플 id_token 서명 검증 실패");
            }

            // 3. Claims 검증
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            // 만료 시간 검증
            if (claims.getExpirationTime().before(new Date())) {
                throw new RuntimeException("애플 id_token 만료됨");
            }

            // audience 검증 (client_id와 일치해야 함)
            if (!claims.getAudience().contains(appleClientId)) {
                throw new RuntimeException("애플 id_token audience 불일치");
            }

            // issuer 검증
            if (!"https://appleid.apple.com".equals(claims.getIssuer())) {
                throw new RuntimeException("애플 id_token issuer 불일치");
            }

            return claims.getClaims();

        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            throw new RuntimeException("애플 id_token 검증 실패", e);
        }
    }
}