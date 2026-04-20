package com.hometalk.onepass.auth.controller;

import com.hometalk.onepass.auth.dto.SignUpDTO;
import com.hometalk.onepass.auth.repository.UserRepository;
import com.hometalk.onepass.auth.service.SignUpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Slf4j
@Controller
@RequiredArgsConstructor
@RequestMapping("/auth/register")
public class SignUpController {

    private final UserRepository userRepository;
    private final SignUpService signUpService;

    @GetMapping("")
    public String Resister(Model model) {
        // step = 1로 초기값 설정
        model.addAttribute("step", 1);

        // 2. 타임리프 th:object와 연결될 빈 DTO 객체 전달
        model.addAttribute("signUpDTO", new SignUpDTO());

        return "auth/register";
    }

    @PostMapping("/signup")   // 회원가입 단계별 목록 처리
    public String signup(
            @ModelAttribute("signUpDTO") @Valid SignUpDTO signUpDTO,      // DTO
            @RequestParam(required = false, defaultValue = "next") String action, // 버튼 상태
            @RequestParam(defaultValue = "1") int currentStep,  // 회원가입 단계
            Model model
    ) {
        if ("next".equals(action)) {
            model.addAttribute("step", currentStep + 1);
            return "auth/register"; // 현재 html 출력
        }

        if ("prev".equals(action)) {
            model.addAttribute("step", currentStep - 1);
            return "auth/register"; // 현재 html 출력 step 2
        }

        if ("complete".equals(action)) {
            // 최종 서비스 로직 호출 (회원가입 처리)
            signUpService.signUp(signUpDTO);
            return "redirect:/auth";
        }

        return "auth/register";
    }

    @GetMapping("/social") // 클래스 레벨의 /auth/register와 합쳐짐
    public String socialSignupForm(@RequestParam String email,
                                   @RequestParam String platform,
                                   @RequestParam String platformId,
                                   Model model) {
        model.addAttribute("email", email);
        model.addAttribute("platform", platform);
        model.addAttribute("platformId", platformId);

        // templates/register-social.html 호출
        return "register-social";
    }

    /**
     * 소셜 가입 완료 처리
     */
    @PostMapping("/social")
    public String registerSocialUser(@RequestParam String email,
                                     @RequestParam String name,
                                     @RequestParam String nickname,
                                     @RequestParam String phone,
                                     @RequestParam String platform,
                                     @RequestParam String platformId) {

        // 소셜 전용 서비스 로직 호출 (User + SocialAccount 생성)
        signUpService.SignUpSocialService(email, name, nickname, phone, platform, platformId);

        return "redirect:/main"; // 또는 완료 페이지
    }
}
