package com.hometalk.onepass.controller;

/*
    홈 페이지 컨트롤러
        → 프로젝트 메인 랜딩 페이지
    URL: GET /hometop/ 또는 /hometop/home
    템플릿: templates/home.html

    비로그인 → home.html (랜딩)
    로그인 상태 → redirect: /dashboard
 */

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping({"/", "/home"})
    public String home(Model model) {
        // 로그인한 사용자는 대시보드로 리다이렉트 - 리다이렉트: 사용자가 요청한 URL을 다른 URL로 자동으로 보내는 것

        // 시드데이터(관련 데이터 모델에 공유 - 추후에) - 시드데이터 : 처음 실행할 때 기본으로 넣어주는 초기 데이터
        return "home";
    }
}