package com.lawding.user.filter;

import com.lawding.user.jwt.JwtProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProvider jwtProvider;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
        FilterChain filterChain) throws ServletException, IOException {

        // 1. 요청의 헤더(Authorization)에서 출입증(토큰)을 쏙 뽑아옵니다.
        String token = resolveToken(request);

        // 2. 토큰이 존재하고, 우리가 만든 기계(JwtProvider)를 통과했다면? (정상 토큰)
        if (token != null && jwtProvider.validateToken(token)) {

            // 3. 토큰을 분해해서 안에 들어있던 유저 ID(PK)를 꺼냅니다.
            Long userId = jwtProvider.getUserIdFromToken(token);

            // 4. 시큐리티에게 "이 녀석은 검증된 녀석이니 통과시켜 줘!" 하고 인증 증명서를 발급해 줍니다.
            // (이때 첫 번째 인자에 userId를 넣었기 때문에, 나중에 Controller에서 이 ID를 바로 꺼내 쓸 수 있습니다!)
            UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(userId, null, Collections.emptyList());

            // 5. 시큐리티 컨텍스트(VIP 라운지)에 증명서를 떡하니 올려둡니다.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }

        // 6. 검사가 끝났으니 다음 필터(혹은 컨트롤러)로 이동시킵니다.
        filterChain.doFilter(request, response);
    }

    // HTTP 요청 헤더에서 "Bearer " 글자를 떼어내고 순수 토큰만 가져오는 헬퍼 메서드
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        // 토큰은 보통 "Bearer eyJhbGciOi..." 형태로 들어옵니다.
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7); // "Bearer " (7글자) 이후의 문자열만 싹둑 자름
        }
        return null;
    }

}
