package com.lawding.auth.client;

import com.nimbusds.jose.jwk.JWKSet;
import java.net.URL;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class ApplePublicKeyClient {

    private static final String APPLE_JWK_URL = "https://appleid.apple.com/auth/keys";

    public JWKSet getApplePublicKeys() {
        try {
            return JWKSet.load(new URL(APPLE_JWK_URL));
        } catch (Exception e) {
            throw new RuntimeException("애플 공개키 조회 실패", e);
        }
    }
}