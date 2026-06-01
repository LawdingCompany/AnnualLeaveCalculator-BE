package com.lawding.user.service;

import com.lawding.user.config.apple.AppleTokenValidator;
import com.lawding.user.dto.CustomOAuth2User;
import com.lawding.user.entity.User;
import com.lawding.user.repository.UserRepository;
import com.lawding.user.userinfo.OAuth2UserInfo;
import com.lawding.user.userinfo.OAuth2UserInfoFactory;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;
    private final AppleTokenValidator appleTokenValidator;

    /**
     * OAuth2 로그인 성공 시 호출되는 메서드
     * <p>
     * Spring Security가 제공하는 DefaultOAuth2UserService를 확장하여 각 소셜 플랫폼(Google, Kakao, Apple)의 사용자 정보를
     * 통일된 형태로 가공하고 DB에 사용자 정보를 저장 또는 업데이트하는 역할을 한다.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        String provider = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName;
        Map<String, Object> attributes;

        if ("apple".equals(provider)) {
            String idToken = (String) userRequest.getAdditionalParameters().get("id_token");
            userNameAttributeName = "sub";
            // 서명 검증 + claims 파싱
            attributes = appleTokenValidator.validateAndParseIdToken(idToken);
        } else {
            OAuth2User oAuth2User = super.loadUser(userRequest);
            attributes = oAuth2User.getAttributes();
            userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails()
                .getUserInfoEndpoint()
                .getUserNameAttributeName();
        }

        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider, attributes);
        User user = saveOrUpdate(userInfo.getEmail(), userInfo.getName(), userInfo.getProvider());

        OAuth2User oAuth2User = new DefaultOAuth2User(
            Collections.singleton(new SimpleGrantedAuthority("ROLE_USER")),
            attributes,
            userNameAttributeName
        );

        return new CustomOAuth2User(oAuth2User, user);
    }

    private User saveOrUpdate(String email, String name, String provider) {
        User user = userRepository.findByEmail(email)
            .map(entity -> {
                if (name != null) {
                    entity.updateName(name);
                }
                entity.updateLastLogin();
                return entity;
            })
            .orElseGet(() -> User.builder()
                .email(email)
                .name(name != null ? name : "Apple User")
                .provider(provider)
                .build());
        return userRepository.save(user);
    }
}
