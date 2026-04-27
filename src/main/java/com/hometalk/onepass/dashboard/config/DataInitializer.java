package com.hometalk.onepass.dashboard.config;

import com.hometalk.onepass.auth.entity.User;
import com.hometalk.onepass.auth.repository.UserRepository;
import com.hometalk.onepass.dashboard.entity.notification.NotificationCommon;
import com.hometalk.onepass.dashboard.enums.AlarmBillingType;
import com.hometalk.onepass.dashboard.enums.AlarmCategory;
import com.hometalk.onepass.dashboard.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component("notificationDataInitializer")
@Order(2)
@ConditionalOnProperty(name = "app.data.init.enabled", havingValue = "true")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("initAlarm started");
        initAlarm();
    }

    private void initAlarm() {

        // 이미 데이터가 있으면 중복 삽입하지 않음
        if (notificationRepository.count() > 0) {
            log.info("[DataInitializer]이미 알람 데이터가 존재합니다. 시드 데이터 삽입을 건너뜁니다.");
            return;
        }

        // 1. 이메일로 유저를 먼저 찾습니다.
        User defaultUser = userRepository.findByEmail("gildong@test.com")
                .orElseGet(() -> {
                    // 2. 만약 없다면, 필수 필드를 모두 채워서 저장합니다.
                    return userRepository.save(User.builder()
                            .name("테스트유저")
                            .email("gildong@test.com")
                            .nickname("테스트닉네임")
                            .phoneNumber("010-0000-0000") // 필수값들
                            .role(User.UserRole.MEMBER)      // Enum 값들
                            .status(User.UserStatus.APPROVED)
                            .build());
                });

        // 샘플 도서 데이터 - Book.builder() 사용
        List<NotificationCommon> sampleAlarm = List.of(
                NotificationCommon.builder()
                        .moduleName("Billing")
                        .categoryType("NEW")
                        .message("2026년 3월분 고지서가 발행되었습니다.")
                        .user(defaultUser)
                        .isRead(false)
                        .build(),
                NotificationCommon.builder()
                        .moduleName("Billing")
                        .categoryType("DUE_7D")
                        .message("관리비 납부 기한이 7일 남았습니다.")
                        .user(defaultUser)
                        .isRead(false)
                        .build(),
                NotificationCommon.builder()
                        .moduleName("Billing")
                        .categoryType("WARN_LONG")
                        .message("관리비 3개월 체납으로 인한 경고 알림입니다. 장기체납 세대는 단전, 단수 및 법적 조치가 발생할 수 있습니다. 관리사무소로 연락 바랍니다. '02-1122-3344'")
                        .user(defaultUser)
                        .isRead(false)
                        .build(),
                NotificationCommon.builder()
                        .moduleName("Billing")
                        .categoryType("NEW")
                        .message("2026년 3월분 고지서가 발행되었습니다.")
                        .user(defaultUser)
                        .isRead(false)
                        .build(),
                NotificationCommon.builder()
                        .moduleName("Billing")
                        .categoryType("NEW")
                        .message("2026년 3월분 고지서가 발행되었습니다.")
                        .user(defaultUser)
                        .isRead(false)
                        .build()

        );

        notificationRepository.saveAll(sampleAlarm);
        log.info("샘플 알람 {}건 삽입 완료.", sampleAlarm.size());
    }
}
