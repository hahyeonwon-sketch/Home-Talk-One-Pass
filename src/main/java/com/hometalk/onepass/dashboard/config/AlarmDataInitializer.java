package com.hometalk.onepass.dashboard.config;

import com.hometalk.onepass.auth.entity.User;
import com.hometalk.onepass.auth.repository.UserRepository;
import com.hometalk.onepass.billing.dto.BillingDetailResponse;
import com.hometalk.onepass.billing.entity.BillingDetail;
import com.hometalk.onepass.billing.entity.BillingStatus;
import com.hometalk.onepass.dashboard.dto.notification.response.NotificationCommonResponseDto;
import com.hometalk.onepass.dashboard.entity.notification.NotificationCommon;
import com.hometalk.onepass.dashboard.entity.notification.NotificationToBilling;
import com.hometalk.onepass.dashboard.entity.notification.NotificationToParking;
import com.hometalk.onepass.dashboard.enums.AlarmCategory;
import com.hometalk.onepass.dashboard.enums.AlarmType;
import com.hometalk.onepass.dashboard.repository.notification.NotificationRepository;
import com.hometalk.onepass.dashboard.repository.notification.NotificationToBillingRepository;
import com.hometalk.onepass.dashboard.repository.notification.NotificationToParkingRepository;
import com.hometalk.onepass.dashboard.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component("notificationDataInitializer")
//@Order(2)
@ConditionalOnProperty(name = "app.data.init.enabled", havingValue = "true")
@RequiredArgsConstructor
public class AlarmDataInitializer implements CommandLineRunner {


    private final NotificationService notificationService;
    private final NotificationToBillingRepository notificationToBillingRepository;
    private final NotificationToParkingRepository notificationToParkingRepository;

    @Override
    public void run(String... args) throws Exception {
        log.info("initAlarm started");
        initAlarm();
    }

    private void initAlarm() {

        boolean isAddBilling = false;
        boolean isAddParking = false;

        User defaultUser = notificationService.getCurrentUser();

        isAddBilling = notificationToBillingRepository.count() <= 0;
        if (isAddBilling) {

            notificationService.isAddNotificationCommonResponseDto(AlarmCategory.BILLING);      // 관리비 데이터가 추가 되었다고 설정을 한다.

            List<NotificationToBilling> sampleBilling = List.of(
                    NotificationToBilling.builder()
                            .alarmCategory(AlarmCategory.BILLING)
                            .alarmType(AlarmType.NEW)
                            .message("새 고지서가 왔습니다.")
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
                            .message("일주일 안에 납부를 희망합니다.")
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
                            .message("독촉 고지서 입니다.")
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
                            .build(),
                    NotificationToBilling.builder()
                            .alarmCategory(AlarmCategory.BILLING)
                            .alarmType(AlarmType.DUE_7D)
                            .message("일주일 안에 납부를 희망합니다.")
                            .user(defaultUser)
                            .billingMonth("2026-02")
                            .totalAmount(BigDecimal.valueOf(155000))
                            .status(BillingStatus.UNPAID)
                            .dueDate(LocalDate.of(2026, 3, 31))
                            .billingItems(List.of(
                                    BillingDetailResponse.ItemDetail.builder()
                                            .itemName("전기료")
                                            .itemAmount(BigDecimal.valueOf(15000))
                                            .build(),
                                    BillingDetailResponse.ItemDetail.builder()
                                            .itemName("수도료")
                                            .itemAmount(BigDecimal.valueOf(25000))
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
                            .message("일주일 안에 납부를 희망합니다.")
                            .user(defaultUser)
                            .billingMonth("2026-01")
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
                                            .itemAmount(BigDecimal.valueOf(50000))
                                            .build()
                            ))
                            .build()
            );

            notificationToBillingRepository.saveAll(sampleBilling);
            log.info("샘플 관리비 알람 {}건 삽입 완료.", sampleBilling.size());
        }
        else {
            // 이미 데이터가 있으면 중복 삽입하지 않음
            log.info("[DataInitializer]이미 관리비 데이터가 존재합니다. 시드 데이터 삽입을 건너뜁니다.");
        }

        isAddParking = notificationToParkingRepository.count() <= 0;
        if (isAddParking) {

            notificationService.isAddNotificationCommonResponseDto(AlarmCategory.PARKING);      // 주차 데이터가 추가 되었다고 설정을 한다.

            List<NotificationToParking> sampleParking = List.of(
                    NotificationToParking.builder()
                            .alarmCategory(AlarmCategory.PARKING)
                            .alarmType(AlarmType.OVER)
                            .message("시간 초과가 되었습니다.")
                            .user(defaultUser)
                            .vehicleNumber("123-0999")
                            .build(),
                    NotificationToParking.builder()
                            .alarmCategory(AlarmCategory.PARKING)
                            .alarmType(AlarmType.REG_APPROVE)
                            .message("등록 승인이 되었습니다.")
                            .user(defaultUser)
                            .vehicleNumber("123-1000")
                            .build(),
                    NotificationToParking.builder()
                            .alarmCategory(AlarmCategory.PARKING)
                            .alarmType(AlarmType.REG_REJECT)
                            .message("등록 거부가 되었습니다.")
                            .user(defaultUser)
                            .vehicleNumber("123-1001")
                            .build(),
                    NotificationToParking.builder()
                            .alarmCategory(AlarmCategory.PARKING)
                            .alarmType(AlarmType.ENTRY_RES)
                            .message("입장 예약이 되었습니다.")
                            .user(defaultUser)
                            .vehicleNumber("123-1002")
                            .build(),
                    NotificationToParking.builder()
                            .alarmCategory(AlarmCategory.PARKING)
                            .alarmType(AlarmType.ENTRY_VIS)
                            .message("입장 방문이 되었습니다.")
                            .user(defaultUser)
                            .vehicleNumber("123-1012")
                            .build(),
                    NotificationToParking.builder()
                            .alarmCategory(AlarmCategory.PARKING)
                            .alarmType(AlarmType.EXIT)
                            .message("출차 되었습니다.")
                            .user(defaultUser)
                            .vehicleNumber("123-1102")
                            .build()
            );

            notificationToParkingRepository.saveAll(sampleParking);
            log.info("샘플 주차 알람 {}건 삽입 완료.", sampleParking.size());
        }
        else {
            // 이미 데이터가 있으면 중복 삽입하지 않음
            log.info("[DataInitializer]이미 주차 데이터가 존재합니다. 시드 데이터 삽입을 건너뜁니다.");
        }
    }
}
