package com.hometalk.onepass.auth.controller;

import com.hometalk.onepass.auth.dto.MyPageResponseDto;
import com.hometalk.onepass.auth.service.MyPageService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class MyPageController {

    private final MyPageService myPageService;

    @GetMapping("/myPage")
    public String myPage(Authentication authentication, Model model) {
        MyPageResponseDto myPage = myPageService.getMyPage(authentication);
        model.addAttribute("myPage", myPage);
        return "auth/my-page";
    }
}
