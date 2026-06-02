package com.hometalk.onepass.dashboard.controller;

import com.hometalk.onepass.auth.dto.MyPageResponseDTO;
import com.hometalk.onepass.auth.service.MyPageService;
import com.hometalk.onepass.dashboard.service.notification.AlarmConfigService;
import com.hometalk.onepass.dashboard.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@RequiredArgsConstructor
public class NotificationApiController {

    private final MyPageService myPageService;
    private final NotificationService notificationService;
    private final AlarmConfigService alarmConfigService;

    // 사용자별 SSE 연결을 저장하는 저장소 (로그인 구현 상태에 따라 Key를 UserId로 관리)
    public static final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    @GetMapping(value = "/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect(Authentication authentication) {

        // 타임아웃 시간을 30분으로 설정하여 생성
        SseEmitter emitter = new SseEmitter(30 * 60 * 1000L);

        if (authentication == null || !authentication.isAuthenticated()) {

            log.warn("[SSE] 비로그인 유저의 연결 요청 - 빈 Emitter 반환");
            SseEmitter dummyEmitter = new SseEmitter(10 * 1000L); // 10초짜리 임시 채널
            dummyEmitter.complete(); // 즉시 종료 처리
            return dummyEmitter;
        }

        MyPageResponseDTO myPage = myPageService.getMyPage(authentication);

        if (myPage == null || myPage.getEmail() == null) {

            log.error("[SSE] 유저 메일 정보를 조회할 수 없음");
            SseEmitter dummyEmitter = new SseEmitter(10 * 1000L);
            dummyEmitter.complete();
            return dummyEmitter;
        }

        String email = myPage.getEmail();
        notificationService.findUserByEmail(email);
        log.info("email == {}", email);

        emitters.put(email, emitter);

        // 연결 해제 및 타임아웃 예외 처리 등록
        emitter.onCompletion(() -> emitters.remove(email));
        emitter.onTimeout(() -> emitters.remove(email));

        // 최초 연결 시 더미 데이터를 한 번 보내주어야 503 에러가 나지 않습니다.
        try {
            emitter.send(SseEmitter.event()
                    .name("connect")
                    .data("connected!"));

            notificationService.sendAlarmSignal();
        } catch (IOException e) {
            emitters.remove(email);
        }

        return emitter;
    }

    @GetMapping("/alarmCheck")
    public ResponseEntity<String> alarmCheck() {

        String alarmSignal = notificationService.getAlarmBadge() ? "NEW_ALARM" : "AllRead_ALARM";
        return ResponseEntity.ok(alarmSignal);
    }

    /**
     * [브라우저 주소창 테스트용 API] 주소창에 직접 입력하여 실시간 알림 신호를 쏩니다.
     * 경로: GET /api/notifications/test-send-get/gildong@test.com
     */
    @GetMapping("/test-send-get/{email}")
    public ResponseEntity<String> testSendSignalGet(@PathVariable String email) {
        // 전역 맵에서 로그인된 유저의 이메일로 SSE 연결을 꺼냅니다.
        SseEmitter emitter = emitters.get(email);

        if (emitter == null) {
            return ResponseEntity.badRequest()
                    .body("실패: 이메일 [" + email + "] 유저는 현재 웹사이트에 접속 중이 아닙니다. (컨트롤러 내 emitters 맵 확인 필요)");
        }

        try {
            // 헤더의 자바스크립트가 기다리는 'alarm-signal' 신호 전송
            emitter.send(SseEmitter.event()
                    .name("alarm-signal")
                    .data("NEW_ALARM"));

            return ResponseEntity.ok("성공: 유저 [" + email + "]에게 실시간 알림 뱃지 신호를 보냈습니다. 원래 화면 탭을 확인하세요!");
        } catch (IOException e) {
            emitters.remove(email);
            return ResponseEntity.internalServerError()
                    .body("실패: 전송 중 오류 발생");
        }
    }
}
