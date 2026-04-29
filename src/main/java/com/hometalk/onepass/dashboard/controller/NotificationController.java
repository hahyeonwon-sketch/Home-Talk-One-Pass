package com.hometalk.onepass.dashboard.controller;

import com.hometalk.onepass.dashboard.dto.notification.response.NotificationCommonResponseDto;
import com.hometalk.onepass.dashboard.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @GetMapping({"/notification"})
    public String notification(Model model,
                               @RequestParam(required = false, defaultValue = "id") String sortBy,
                               @RequestParam(required = false, defaultValue = "desc") String direction,
                               @PageableDefault(size = 3) Pageable pageable) {


        Page<NotificationCommonResponseDto> isNotReadPage = null;  // 최종적으로 뷰에 전달할 회원 목록
        Page<NotificationCommonResponseDto> isReadPage = null;  // 최종적으로 뷰에 전달할 회원 목록

        // "asc" 외 모든 값은 DESC 처리
        Sort.Direction dir = "asc".equalsIgnoreCase(direction)
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        // 정렬 허용 목록 컬럼명인데, 허용 목록에 없는 컬럼명은 "id"로 강제 변환
        String validSort =
                List.of("id", "isRead", "createdAt").contains(sortBy)
                        ? sortBy : "id";

        // @PageableDefault의 page/size + 위에서 결정한 정렬 기준을 합쳐 새 Pageable 생성
        Pageable sortedPageable =
                PageRequest.of(pageable.getPageNumber(), // @PageableDefault가 만들어준 page 번호
                        pageable.getPageSize(),  // @PageableDefault가 만들어준 size (기본10)
                        Sort.by(dir, validSort));  // 정렬은 새로 적용

        isNotReadPage = notificationService.findByIsNotReadNotification(sortedPageable);
        isReadPage = notificationService.findByIsReadNotification(sortedPageable);

        model.addAttribute("isNotReadAlarmList", isNotReadPage);    // 안 읽은 보여주는 알림
        model.addAttribute("isReadAlarmList", isReadPage);          // 읽은 보여주는 알림
        model.addAttribute("sortBy", validSort);
        model.addAttribute("direction", direction);

        // 시드 데이터 (관련 데이터 모델에 공유 - 추후)
        return "/notification/main";
    }
}

