package com.hometalk.onepass.auth.config;

import com.hometalk.onepass.auth.entity.SocialAccount;
import com.hometalk.onepass.auth.repository.SocialAccountRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final SocialAccountRepository socialAccountRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();

        // 1. 등록된 서비스(kakao, google, naver) 식별
        String registrationId = ((OAuth2AuthenticationToken) authentication).getAuthorizedClientRegistrationId();
        SocialAccount.Platform platform = SocialAccount.Platform.valueOf(registrationId.toUpperCase());

        // 2. 플랫폼별로 다른 고유 ID(platformId) 및 이메일 추출 로직
        String platformId = "";
        String email = "";

        if (platform == SocialAccount.Platform.KAKAO) {
            platformId = String.valueOf(oAuth2User.getAttributes().get("id"));
            Map<String, Object> kakaoAccount = (Map<String, Object>) oAuth2User.getAttributes().get("kakao_account");
            email = (String) kakaoAccount.get("email");
        }
        else if (platform == SocialAccount.Platform.NAVER) {
            Map<String, Object> responseData = (Map<String, Object>) oAuth2User.getAttributes().get("response");
            platformId = (String) responseData.get("id");
            email = (String) responseData.get("email");
        }
        else if (platform == SocialAccount.Platform.GOOGLE) {
            platformId = (String) oAuth2User.getAttributes().get("sub");
            email = (String) oAuth2User.getAttributes().get("email");
        }

        // 3. DB 확인: 해당 플랫폼의 고유 ID가 이미 존재하는지 체크
        Optional<SocialAccount> socialAccount = socialAccountRepository.findByPlatformAndPlatformId(platform, platformId);

        if (socialAccount.isEmpty()) {
            // 신규 유저 -> 추가 정보 입력 페이지로 이동
            // 파라미터에 platform 정보도 포함하여 전송 (가입 완료 시 필요)
            String redirectUrl = String.format("/register/social?email=%s&platform=%s&platformId=%s",
                    email, platform, platformId);
            getRedirectStrategy().sendRedirect(request, response, redirectUrl);
        } else {
            // 기존 유저 -> 로그인 처리 및 메인 이동
            getRedirectStrategy().sendRedirect(request, response, "/index");
        }
    }
}