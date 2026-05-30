package com.lawding.user.service;

import com.lawding.user.dto.CustomOAuth2User;
import com.lawding.user.entity.User;
import com.lawding.user.repository.UserRepository;
import com.lawding.user.userinfo.AppleOAuth2UserInfo;
import com.lawding.user.userinfo.GoogleOAuth2UserInfo;
import com.lawding.user.userinfo.KakaoOAuth2UserInfo;
import com.lawding.user.userinfo.OAuth2UserInfo;
import com.lawding.user.userinfo.OAuth2UserInfoFactory;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    private final UserRepository userRepository;

    /**
     * OAuth2 로그인 성공 시 호출되는 메서드
     * <p>
     * Spring Security가 제공하는 DefaultOAuth2UserService를 확장하여 각 소셜 플랫폼(Google, Kakao, Apple)의 사용자 정보를
     * 통일된 형태로 가공하고 DB에 사용자 정보를 저장 또는 업데이트하는 역할을 한다.
     */
    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {

        // OAuth2 Provider(Google, Kakao, Apple)로부터 사용자 정보 조회
        OAuth2User oAuth2User = super.loadUser(userRequest);
        String provider = userRequest.getClientRegistration().getRegistrationId();

        // 1. 공장(Factory)을 통해 플랫폼별로 데이터를 깔끔하게 추출!
        OAuth2UserInfo userInfo = OAuth2UserInfoFactory.getOAuth2UserInfo(provider,
            oAuth2User.getAttributes());

        // 2. DB에 저장 또는 업데이트
        User user = saveOrUpdate(userInfo.getEmail(), userInfo.getName(), userInfo.getProvider());

        // ✨ 3. 단순 껍데기가 아니라, 우리 User 객체를 품은 캡슐로 포장해서 리턴!
        return new CustomOAuth2User(oAuth2User, user);
    }
    private User saveOrUpdate(String email, String name, String provider) {
        User user = userRepository.findByEmail(email)
            .map(entity -> entity.updateName(name)) // 이미 있으면 이름만 업데이트
            .orElse(User.builder()                  // 없으면 새로 가입
                .email(email)
                .name(name)
                .provider(provider)
                .build());
        return userRepository.save(user);
    }
}
