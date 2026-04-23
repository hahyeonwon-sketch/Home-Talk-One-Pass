package com.hometalk.onepass.auth.service;

import com.hometalk.onepass.auth.dto.MyPageResponseDto;
import com.hometalk.onepass.auth.entity.Household;
import com.hometalk.onepass.auth.entity.LocalAccount;
import com.hometalk.onepass.auth.entity.SocialAccount;
import com.hometalk.onepass.auth.entity.User;
import com.hometalk.onepass.auth.repository.LocalAccountRepository;
import com.hometalk.onepass.auth.repository.SocialAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {

    private final LocalAccountRepository localAccountRepository;
    private final SocialAccountRepository socialAccountRepository;

    public MyPageResponseDto getMyPage(Authentication authentication) {
        if (authentication == null
                || !authentication.isAuthenticated()
                || authentication instanceof AnonymousAuthenticationToken) {
            throw new IllegalStateException("로그인한 사용자만 마이페이지를 조회할 수 있습니다.");
        }

        if (authentication instanceof OAuth2AuthenticationToken oauthToken
                && authentication.getPrincipal() instanceof OAuth2User oAuth2User) {
            return buildSocialMyPage(oauthToken, oAuth2User);
        }

        return buildLocalMyPage(authentication.getName());
    }

    private MyPageResponseDto buildLocalMyPage(String loginId) {
        LocalAccount account = localAccountRepository.findByLoginId(loginId)
                .orElseThrow(() -> new IllegalStateException("로컬 계정 정보를 찾을 수 없습니다."));

        return toDto(account.getUser(), "일반 로그인", account.getLoginId(), null);
    }

    private MyPageResponseDto buildSocialMyPage(OAuth2AuthenticationToken oauthToken, OAuth2User oAuth2User) {
        String registrationId = oauthToken.getAuthorizedClientRegistrationId();
        SocialAccount.Platform platform = SocialAccount.Platform.valueOf(registrationId.toUpperCase());
        String email = extractEmail(platform, oAuth2User.getAttributes());

        if (email == null || email.isBlank()) {
            throw new IllegalStateException("소셜 계정 이메일 정보를 찾을 수 없습니다.");
        }

        String combinedPlatformId = email + "_" + registrationId.toUpperCase();

        SocialAccount socialAccount = socialAccountRepository.findByPlatformAndPlatformId(platform, combinedPlatformId)
                .orElseThrow(() -> new IllegalStateException("소셜 계정 정보를 찾을 수 없습니다."));

        return toDto(socialAccount.getUser(), "소셜 로그인", null, platform.name());
    }

    @SuppressWarnings("unchecked")
    private String extractEmail(SocialAccount.Platform platform, Map<String, Object> attributes) {
        if (platform == SocialAccount.Platform.KAKAO) {
            Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
            return kakaoAccount == null ? null : (String) kakaoAccount.get("email");
        }

        if (platform == SocialAccount.Platform.NAVER) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");
            return response == null ? null : (String) response.get("email");
        }

        return (String) attributes.get("email");
    }

    private MyPageResponseDto toDto(User user, String authType, String loginId, String socialPlatform) {
        Household household = user.getHousehold();

        return MyPageResponseDto.builder()
                .authType(authType)
                .loginId(loginId)
                .socialPlatform(socialPlatform)
                .name(user.getName())
                .nickname(user.getNickname())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .role(user.getRole().name())
                .status(user.getStatus().name())
                .buildingName(household != null ? household.getBuildingName() : null)
                .dong(household != null ? household.getDong() : null)
                .ho(household != null ? household.getHo() : null)
                .postNum(household != null ? household.getPostNum() : null)
                .build();
    }
}
