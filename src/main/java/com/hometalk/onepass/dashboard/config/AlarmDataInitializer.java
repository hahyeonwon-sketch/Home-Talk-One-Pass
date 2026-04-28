package com.hometalk.onepass.dashboard.config;

import com.hometalk.onepass.auth.entity.User;
import com.hometalk.onepass.auth.repository.UserRepository;
import com.hometalk.onepass.dashboard.entity.notification.NotificationCommon;
import com.hometalk.onepass.dashboard.enums.AlarmCategory;
import com.hometalk.onepass.dashboard.enums.AlarmType;
import com.hometalk.onepass.dashboard.repository.notification.NotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component("notificationDataInitializer")
//@Order(2)
@ConditionalOnProperty(name = "app.data.init.enabled", havingValue = "true")
@RequiredArgsConstructor
public class AlarmDataInitializer implements CommandLineRunner {

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

        List<NotificationCommon> sampleAlarm = List.of(
                NotificationCommon.builder()
                        .alarmCategory(AlarmCategory.BILLING)
                        .alarmType(AlarmType.NEW)
                        .message("고지서 지금 확인하세요")
                        .user(defaultUser)
                        .isRead(false)
                        .build(),
                NotificationCommon.builder()
                        .alarmCategory(AlarmCategory.BILLING)
                        .alarmType(AlarmType.DUE_7D)
                        .message("고지서 지금 확인하세요")
                        .user(defaultUser)
                        .isRead(false)
                        .build(),
                NotificationCommon.builder()
                        .alarmCategory(AlarmCategory.BILLING)
                        .alarmType(AlarmType.WARN_LONG)
                        .message("고지서 지금 확인하세요")
                        .user(defaultUser)
                        .isRead(false)
                        .build(),
                NotificationCommon.builder()
                        .alarmCategory(AlarmCategory.BILLING)
                        .alarmType(AlarmType.NEW)
                        .message("고지서 지금 확인하세요")
                        .user(defaultUser)
                        .isRead(false)
                        .build(),
                NotificationCommon.builder()
                        .alarmCategory(AlarmCategory.BILLING)
                        .alarmType(AlarmType.NEW)
                        .message("고지서 지금 확인하세요")
                        .user(defaultUser)
                        .isRead(false)
                        .build()

        );

        notificationRepository.saveAll(sampleAlarm);
        log.info("샘플 알람 {}건 삽입 완료.", sampleAlarm.size());
    }
}
