package com.hometalk.onepass.auth.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private final String kakaoClientId;

    public CustomLogoutSuccessHandler(
            @Value("${spring.security.oauth2.client.registration.kakao.client-id}") String kakaoClientId) {
        this.kakaoClientId = kakaoClientId;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
                                Authentication authentication) throws IOException, ServletException {

        if (authentication instanceof OAuth2AuthenticationToken oauth2AuthenticationToken
                && "kakao".equals(oauth2AuthenticationToken.getAuthorizedClientRegistrationId())) {

            String logoutRedirectUri = UriComponentsBuilder.fromUriString(getBaseUrl(request))
                    .path(request.getContextPath())
                    .path("/auth")
                    .build()
                    .toUriString();

            String kakaoLogoutUrl = UriComponentsBuilder
                    .fromUriString("https://kauth.kakao.com/oauth/logout")
                    .queryParam("client_id", kakaoClientId)
                    .queryParam("logout_redirect_uri", logoutRedirectUri)
                    .build()
                    .toUriString();

            response.sendRedirect(kakaoLogoutUrl);
            return;
        }

        response.sendRedirect(request.getContextPath() + "/auth");
    }

    private String getBaseUrl(HttpServletRequest request) {
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }
}
