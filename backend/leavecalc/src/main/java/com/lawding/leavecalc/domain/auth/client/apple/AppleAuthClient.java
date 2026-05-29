package com.lawding.leavecalc.domain.auth.client.apple;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

@Component
@RequiredArgsConstructor
public class AppleAuthClient {

    private final WebClient webClient = WebClient.create("https://appleid.apple.com");

    public AppleTokenResponse getToken(String authorizationCode, String clientSecret,
        String clientId, String redirectUri) {
        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("code", authorizationCode);
        formData.add("grant_type", "authorization_code");
        formData.add("redirect_uri", redirectUri);

        return webClient.post()
            .uri("/auth/token")
            .header("Content-Type", "application/x-www-form-urlencoded")
            .body(BodyInserters.fromFormData(formData))
            .retrieve()
            .bodyToMono(AppleTokenResponse.class)
            .block();
    }
}
