package com.hometalk.onepass.auth.controller;

import com.hometalk.onepass.auth.dto.MyPageResponseDTO;
import com.hometalk.onepass.auth.service.MyPageService;
import com.hometalk.onepass.auth.service.WithdrawalService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;
    private final WithdrawalService withdrawalService;

    @GetMapping("/myPage")
    public String myPage(Authentication authentication, Model model) {
        MyPageResponseDTO myPage = myPageService.getMyPage(authentication);
        model.addAttribute("myPage", myPage);
        return "auth/my-page";
    }

    @PostMapping("/auth/withdraw")
    public String withdraw(Authentication authentication,
                           HttpServletRequest request,
                           HttpServletResponse response) {
        withdrawalService.withdraw(authentication);
        new SecurityContextLogoutHandler().logout(request, response, authentication);
        return "redirect:/auth?withdrawn=true";
    }
}
