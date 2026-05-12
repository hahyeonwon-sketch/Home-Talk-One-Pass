package com.hometalk.onepass.dashboard.controller;

import com.hometalk.onepass.auth.dto.MyPageResponseDTO;
import com.hometalk.onepass.auth.entity.User;
import com.hometalk.onepass.auth.service.MyPageService;
import com.hometalk.onepass.dashboard.dto.notification.response.NotificationCommonResponseDto;
import com.hometalk.onepass.dashboard.dto.notification.response.NotificationToBillingDto;
import com.hometalk.onepass.dashboard.dto.notification.response.NotificationToParkingDto;
import com.hometalk.onepass.dashboard.entity.notification.NotificationToBilling;
import com.hometalk.onepass.dashboard.enums.AlarmCategory;
import com.hometalk.onepass.dashboard.repository.notification.NotificationRepository;
import com.hometalk.onepass.dashboard.repository.notification.NotificationToBillingRepository;
import com.hometalk.onepass.dashboard.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Slf4j
@Controller
@RequestMapping("/notification")
@RequiredArgsConstructor
public class NotificationController {

    private final MyPageService myPageService;
    private final NotificationService notificationService;

//    private final NotificationRepository notificationRepository;
//    private final NotificationToBillingRepository notificationToBillingRepository;

    @GetMapping
    public String notification(
            Authentication authentication,
            Model model,
            @RequestParam(required = false, defaultValue = "ALL") AlarmCategory alarmCategory,
            @RequestParam(required = false, defaultValue = "id") String sortBy,
            @RequestParam(required = false, defaultValue = "desc") String direction,
            @Qualifier("first") @PageableDefault(size = 3) Pageable firstPageable,
            @Qualifier("second") @PageableDefault(size = 3) Pageable secondPageable) {

        MyPageResponseDTO myPage = myPageService.getMyPage(authentication);
        log.info("email == {}", myPage.getEmail());
        notificationService.findNotificationByEmail(myPage.getEmail());


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
        Pageable first_sortedPageable =
                PageRequest.of(firstPageable.getPageNumber(), // @PageableDefault가 만들어준 page 번호
                        firstPageable.getPageSize(),  // @PageableDefault가 만들어준 size (기본10)
                        Sort.by(dir, validSort));  // 정렬은 새로 적용

        Pageable second_sortedPageable =
                PageRequest.of(secondPageable.getPageNumber(), // @PageableDefault가 만들어준 page 번호
                        secondPageable.getPageSize(),  // @PageableDefault가 만들어준 size (기본10)
                        Sort.by(dir, validSort));  // 정렬은 새로 적용

        Map<String, Page<NotificationCommonResponseDto>> alarmMap = new HashMap<>();

        log.info("alarmCategory == {}", alarmCategory.name());
        if (alarmCategory == AlarmCategory.ALL) {     // 카테고리 검색을 전체로 하는 경우

            isNotReadPage = notificationService.findByIsNotReadNotification(first_sortedPageable);  // 안 읽은 보여주는 알림
            isReadPage = notificationService.findByIsReadNotification(second_sortedPageable);   // 읽은 보여주는 알림

            alarmMap.put("Y", isNotReadPage);
            alarmMap.put("N", isReadPage);
        } else {    // 카테고리 검색을 전체로 하지 않는 경우

            isNotReadPage = notificationService.findByIsNotReadNotification(first_sortedPageable);  // 안 읽은 보여주는 알림
            isReadPage = notificationService.findByIsAlarmCategory(true, alarmCategory, second_sortedPageable);

            alarmMap.put("Y", isNotReadPage);
            alarmMap.put("N", isReadPage);
        }

        model.addAttribute("alarmMap", alarmMap);
        model.addAttribute("sortBy", validSort);
        model.addAttribute("direction", direction);
        model.addAttribute("alarmCategory", alarmCategory);

        // 시드 데이터 (관련 데이터 모델에 공유 - 추후)
        return "/notification/main";
    }

    /*
     *   알림 상세 페이지
     *   GET /notification/{id}
     * */
    @GetMapping("/detail/{id}")
    public String detail(
            Model model,
            @PathVariable Long id,
            @RequestParam(required = false, defaultValue = "true") boolean isRead) {

        NotificationCommonResponseDto notiCommonResponseDto = notificationService.saveNotification(id, isRead);
        log.info("== 알림 상세 조회 == id={}, AlarmCategory={}", notiCommonResponseDto.getId(), notiCommonResponseDto.getAlarmCategory());

        Object detailObj = notificationService.findNotificationToDetailById(id);

        if (notiCommonResponseDto.getAlarmCategory() == AlarmCategory.BILLING)
        {
            NotificationToBillingDto detail = (NotificationToBillingDto) detailObj;
            model.addAttribute("detail",  detail);
        }
        else if (notiCommonResponseDto.getAlarmCategory() == AlarmCategory.PARKING)
        {
            NotificationToParkingDto detail = (NotificationToParkingDto) detailObj;
            model.addAttribute("detail",  detail);
        }


        return "/notification/detail";   // templates/notification/detail.html
    }

    //알림 삭제
    @PostMapping("/{alarmCategory}/{commonId}/{detailId}/delete")
    public String deleteNotification(
            @PathVariable AlarmCategory alarmCategory,
            @PathVariable Long commonId,
            @PathVariable Long detailId) {
        log.info("== 공통 알림 삭제 == id={}", commonId);
        log.info("== 디테일 알림 삭제 == id={}", detailId);
        notificationService.deleteNotificationById(alarmCategory, commonId, detailId);
        return "redirect:/notification?deleted=true";
    }
}

