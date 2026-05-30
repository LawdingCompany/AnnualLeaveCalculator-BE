package com.lawding.user.service;

import com.lawding.user.entity.User;
import com.lawding.user.repository.UserRepository;
import com.lawding.user.userinfo.AppleOAuth2UserInfo;
import com.lawding.user.userinfo.GoogleOAuth2UserInfo;
import com.lawding.user.userinfo.KakaoOAuth2UserInfo;
import com.lawding.user.userinfo.OAuth2UserInfo;
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

        // 어떤 소셜 로그인인지 구분 (google / kakao / apple)
        String provider = userRequest.getClientRegistration().getRegistrationId();
        OAuth2UserInfo userInfo = getOAuth2UserInfo(oAuth2User, provider);

        // 2. 인터페이스를 통해 일관된 방식으로 데이터 추출 및 DB 저장
        User user = saveOrUpdate(userInfo.getEmail(), userInfo.getName(), userInfo.getProvider());

        return oAuth2User; // 시큐리티 세션에 정보 저장
    }

    private OAuth2UserInfo getOAuth2UserInfo(OAuth2User oAuth2User, String provider) {
        Map<String, Object> attributes = oAuth2User.getAttributes();

        OAuth2UserInfo userInfo = null;
        if ("google".equals(provider)) {
            userInfo = new GoogleOAuth2UserInfo(attributes);
        } else if ("kakao".equals(provider)) {
            userInfo = new KakaoOAuth2UserInfo(attributes);
        } else if ("apple".equals(provider)) {
            userInfo = new AppleOAuth2UserInfo(attributes);
        } else {
            throw new OAuth2AuthenticationException("지원하지 않는 소셜 로그인입니다.");
        }
        return userInfo;
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
