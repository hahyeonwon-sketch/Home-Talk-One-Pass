package com.hometalk.onepass.dashboard.controller;

import com.hometalk.onepass.dashboard.dto.notification.response.NotificationCommonResponseDto;
import com.hometalk.onepass.dashboard.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping({"/notification"})
    public String notification(Model model) {

        List<NotificationCommonResponseDto> isNotReadAlarmList;     // 뷰에 전달한 알람 리스트
        List<NotificationCommonResponseDto> isReadAlarmList;        // 뷰에 전달한 알람 리스트

        isNotReadAlarmList = notificationService.findByIsReadFalseOrderByCreatedAtDesc();
        isReadAlarmList = notificationService.findByIsReadTrueOrderByCreatedAtDesc();

        model.addAttribute("isNotReadAlarmList", isNotReadAlarmList);
        model.addAttribute("isReadAlarmList", isReadAlarmList);

        // 시드 데이터 (관련 데이터 모델에 공유 - 추후)
        return "/notification/main";
    }
}

