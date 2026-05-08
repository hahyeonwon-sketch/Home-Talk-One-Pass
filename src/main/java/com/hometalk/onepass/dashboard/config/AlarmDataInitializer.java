package com.hometalk.onepass.dashboard.config;

import com.hometalk.onepass.auth.entity.User;
import com.hometalk.onepass.auth.repository.UserRepository;
import com.hometalk.onepass.billing.dto.BillingDetailResponse;
import com.hometalk.onepass.billing.entity.BillingDetail;
import com.hometalk.onepass.billing.entity.BillingStatus;
import com.hometalk.onepass.dashboard.entity.notification.NotificationCommon;
import com.hometalk.onepass.dashboard.entity.notification.NotificationToBilling;
import com.hometalk.onepass.dashboard.enums.AlarmCategory;
import com.hometalk.onepass.dashboard.enums.AlarmType;
import com.hometalk.onepass.dashboard.repository.notification.NotificationRepository;
import com.hometalk.onepass.dashboard.repository.notification.NotificationToBillingRepository;
import com.hometalk.onepass.dashboard.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("notificationDataInitializer")
//@Order(2)
@ConditionalOnProperty(name = "app.data.init.enabled", havingValue = "true")
@RequiredArgsConstructor
public class AlarmDataInitializer implements CommandLineRunner {


    private final NotificationToBillingRepository notificationToBillingRepository;
    private final NotificationService notificationService;

    @Override
    public void run(String... args) throws Exception {
        log.info("initAlarm started");
        initAlarm();
    }

    private void initAlarm() {

        // 이미 데이터가 있으면 중복 삽입하지 않음
        if (notificationToBillingRepository.count() > 0) {
            log.info("[DataInitializer]이미 관리비 데이터가 존재합니다. 시드 데이터 삽입을 건너뜁니다.");
            return;
        }

        User defaultUser = notificationService.findUserByEmail();

        List<NotificationToBilling> sampleBilling = List.of(
                NotificationToBilling.builder()
                        .alarmCategory(AlarmCategory.BILLING)
                        .alarmType(AlarmType.NEW)
                        .message("고지서 지금 확인하세요")
                        .user(defaultUser)
                        .billingMonth("2026-03")
                        .totalAmount(BigDecimal.valueOf(155000))
                        .status(BillingStatus.UNPAID)
                        .dueDate(LocalDate.of(2026, 3, 31))
                        .billingItems(List.of(
                                BillingDetailResponse.ItemDetail.builder()
                                        .itemName("전기료")
                                        .itemAmount(BigDecimal.valueOf(20000))
                                        .build(),
                                BillingDetailResponse.ItemDetail.builder()
                                        .itemName("난방비")
                                        .itemAmount(BigDecimal.valueOf(30000))
                                        .build()
                        ))
                        .build(),
                NotificationToBilling.builder()
                        .alarmCategory(AlarmCategory.BILLING)
                        .alarmType(AlarmType.DUE_7D)
                        .message("고지서 지금 확인하세요")
                        .user(defaultUser)
                        .billingMonth("2026-04")
                        .totalAmount(BigDecimal.valueOf(155000))
                        .status(BillingStatus.UNPAID)
                        .dueDate(LocalDate.of(2026, 3, 31))
                        .billingItems(List.of(
                                BillingDetailResponse.ItemDetail.builder()
                                        .itemName("전기료")
                                        .itemAmount(BigDecimal.valueOf(10000))
                                        .build(),
                                BillingDetailResponse.ItemDetail.builder()
                                        .itemName("수도료")
                                        .itemAmount(BigDecimal.valueOf(20000))
                                        .build()
                        ))
                        .build(),
                NotificationToBilling.builder()
                        .alarmCategory(AlarmCategory.BILLING)
                        .alarmType(AlarmType.WARN_LONG)
                        .message("고지서 지금 확인하세요")
                        .user(defaultUser)
                        .billingMonth("2026-05")
                        .totalAmount(BigDecimal.valueOf(155000))
                        .status(BillingStatus.UNPAID)
                        .dueDate(LocalDate.of(2026, 3, 31))
                        .billingItems(List.of(
                                BillingDetailResponse.ItemDetail.builder()
                                        .itemName("전기료")
                                        .itemAmount(BigDecimal.valueOf(50000))
                                        .build(),
                                BillingDetailResponse.ItemDetail.builder()
                                        .itemName("수도료")
                                        .itemAmount(BigDecimal.valueOf(30000))
                                        .build(),
                                BillingDetailResponse.ItemDetail.builder()
                                        .itemName("난방비")
                                        .itemAmount(BigDecimal.valueOf(40000))
                                        .build()
                        ))
                        .build()
        );

        notificationToBillingRepository.saveAll(sampleBilling);
        log.info("샘플 관리비 알람 {}건 삽입 완료.", sampleBilling.size());
    }
}
