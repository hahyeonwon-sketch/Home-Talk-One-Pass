package com.hometalk.onepass.dashboard.service.notification.impl;


import com.hometalk.onepass.auth.entity.User;
import com.hometalk.onepass.auth.repository.UserRepository;
import com.hometalk.onepass.billing.entity.BillingStatus;
import com.hometalk.onepass.community.entity.Category;
import com.hometalk.onepass.dashboard.dto.notification.response.NotificationCommonResponseDto;
import com.hometalk.onepass.dashboard.dto.notification.response.NotificationToBillingDto;
import com.hometalk.onepass.dashboard.dto.notification.response.NotificationToParkingDto;
import com.hometalk.onepass.dashboard.entity.notification.NotificationCommon;
import com.hometalk.onepass.dashboard.entity.notification.NotificationToBilling;
import com.hometalk.onepass.dashboard.entity.notification.NotificationToParking;
import com.hometalk.onepass.dashboard.enums.AlarmCategory;
import com.hometalk.onepass.dashboard.enums.AlarmType;
import com.hometalk.onepass.dashboard.repository.notification.NotificationRepository;
import com.hometalk.onepass.dashboard.repository.notification.NotificationToBillingRepository;
import com.hometalk.onepass.dashboard.repository.notification.NotificationToParkingRepository;
import com.hometalk.onepass.dashboard.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{


    // 유저 관련 DB
    private final UserRepository userRepository;

    // 알림 관련 DB 접근을 담당하는 Repository
    private final NotificationRepository notificationRepository;
    private final NotificationToBillingRepository notificationToBillingRepository;
    private final NotificationToParkingRepository notificationToParkingRepository;

    // 공통 DB 추가 가능 여부
    private final Set<AlarmCategory> isAddNotificationCommonSet = new HashSet<>();


    @Override
    @Transactional(readOnly = true)   // 읽기 전용 트랜잭션 -> Hibernate 더티 체킹(변경 감지) 생략으로 성능 향상
    public List<NotificationCommonResponseDto> findByIsNotReadNotification() {

        return notificationRepository.findByIsRead(false)   // false를 전달하여 '읽지 않은' 알림 조회
                .stream()
                .map(NotificationCommonResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public Page<NotificationCommonResponseDto> findByIsNotReadNotification(Pageable pageable) {

        return notificationRepository.findByIsRead(false, pageable)  // deleted_at IS NULL + LIMIT/OFFSET/ORDER BY 자동 생성
                .map(NotificationCommonResponseDto::from);        // Page<NotificationCommonResponseDto> -> Page<NotificationCommonResponseDto> 변환 (메타정보 유지)
    }

    @Override
    @Transactional(readOnly = true)   // 읽기 전용 트랜잭션 -> Hibernate 더티 체킹(변경 감지) 생략으로 성능 향상
    public List<NotificationCommonResponseDto> findByIsReadNotification() {

        return notificationRepository.findByIsRead(true)    // true를 전달하여 '읽은' 알림 조회
                .stream()
                .map(NotificationCommonResponseDto::from)
                .collect(Collectors.toList());
    }

    @Override
    public Page<NotificationCommonResponseDto> findByIsReadNotification(Pageable pageable) {

        return notificationRepository.findByIsRead(true, pageable)  // deleted_at IS NULL + LIMIT/OFFSET/ORDER BY 자동 생성
                .map(NotificationCommonResponseDto::from);        // Page<NotificationCommon -> Page<NotificationCommonResponseDto> 변환 (메타정보 유지)
    }

    @Override
    public Page<NotificationCommonResponseDto> findByIsAlarmCategory(boolean isRead, AlarmCategory alarmCategory, Pageable pageable) {

        return notificationRepository.findByIsReadAndAlarmCategory(isRead, alarmCategory, pageable)
                .map(NotificationCommonResponseDto::from);
    }

    @Override
    public Page<NotificationCommonResponseDto> findNotificationsByUrgent(boolean status, Pageable pageable) {

        return notificationRepository.findNotificationsByUrgent(status, pageable)
                .map(NotificationCommonResponseDto::from);
    }

    @Override
    public NotificationCommonResponseDto findNotificationCommonById(long id) {

        NotificationCommon NotificationCommon = notificationRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 알림을 찾을 수 없습니다. id= " + id));
        return  NotificationCommonResponseDto.from(NotificationCommon);
    }

    @Override
    public Object findNotificationToDetailById(long id) {

        NotificationCommon notificationCommon = notificationRepository.findById(id)
                .orElseThrow(() ->
                        new NoSuchElementException("해당 알림을 찾을 수 없습니다. id= " + id));

        Object detailObj = null;
        switch (notificationCommon.getAlarmCategory()) {
            case BILLING:
                NotificationToBilling notificationToBilling = notificationToBillingRepository.findById(notificationCommon.getReferenceId())
                        .orElseThrow(() ->
                                new NoSuchElementException("해당 관리비를 찾을 수 없습니다. referenceId " + notificationCommon.getReferenceId()));

                detailObj = NotificationToBillingDto.from(notificationToBilling);
                break;
            case PARKING:
                NotificationToParking notificationToParking = notificationToParkingRepository.findById(notificationCommon.getReferenceId())
                        .orElseThrow(() ->
                                new NoSuchElementException("해당 주차를 찾을 수 없습니다. referenceId " + notificationCommon.getReferenceId()));

                detailObj = NotificationToParkingDto.from(notificationToParking);
                break;
            case NOTICE:
            case SCHEDULE:
            case COMMUNICATION:
            case INQUIRY:
            case FACILITY:
            case RESERVATION:
            default:
        }

        return detailObj;
    }

    @Override
    public User findUserByEmail() {

        return userRepository.findByEmail("gildong@test.com")
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
    }

    @Override
    public NotificationCommonResponseDto saveNotification(Long id, boolean isRead) {

        // 1. 기존 데이터를 DB에서 조회 (영속화)
        NotificationCommon entity = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 알림이 없습니다."));

        entity.setIsRead(isRead);
        notificationRepository.save(entity);

        return NotificationCommonResponseDto.from(entity);
    }

    @Override
    public void findNotificationByEmail(String email) {

        boolean isAddBilling = false;
        boolean isAddParking = false;

        List<NotificationCommon> sampleAlarmList = new ArrayList<>();
        isAddBilling = isAddNotificationCommonSet.contains(AlarmCategory.BILLING);
        log.info("샘플 관리비 공통 현재 갯수 = {}", notificationToBillingRepository.count());
        if (isAddBilling) {

            List<NotificationToBilling> unpaidBillingList =
                    notificationToBillingRepository.findByUserEmailAndStatus(email, BillingStatus.UNPAID);

            for (NotificationToBilling notificationToBilling : unpaidBillingList) {

                sampleAlarmList.add(
                        NotificationCommon.builder()
                                .alarmCategory(notificationToBilling.getAlarmCategory())
                                .alarmType(notificationToBilling.getAlarmType())
                                .referenceId(notificationToBilling.getId())
                                .message(notificationToBilling.getMessage())
                                .user(notificationToBilling.getUser())
                                .isRead(false)
                                .build()
                );
            }
        }
        else {
            // 이미 데이터가 있으면 중복 삽입하지 않음
            log.info("[DataInitializer]이미 관리비 공통 데이터가 존재합니다. 시드 데이터 삽입을 건너뜁니다.");
        }

        isAddParking = isAddNotificationCommonSet.contains(AlarmCategory.PARKING);
        log.info("샘플 주차 공통 현재 갯수 = {}", notificationToParkingRepository.count());
        if (isAddParking) {

            List<NotificationToParking> parkingList =
                    notificationToParkingRepository.findByUserEmailAndStatus(email);

            for (NotificationToParking notificationToParking : parkingList) {

                sampleAlarmList.add(
                        NotificationCommon.builder()
                                .alarmCategory(notificationToParking.getAlarmCategory())
                                .alarmType(notificationToParking.getAlarmType())
                                .referenceId(notificationToParking.getId())
                                .message(notificationToParking.getMessage())
                                .user(notificationToParking.getUser())
                                .isRead(false)
                                .build()
                );
            }
        }
        else {
            // 이미 데이터가 있으면 중복 삽입하지 않음
            log.info("[DataInitializer]이미 주차 공통 데이터가 존재합니다. 시드 데이터 삽입을 건너뜁니다.");
        }


        if (isAddBilling || isAddParking) {
            isAddNotificationCommonSet.clear();
            notificationRepository.saveAll(sampleAlarmList);
            log.info("샘플 공통 알람 {}건 삽입 완료.", sampleAlarmList.size());
        }
    }

    @Override
    @Transactional
    public void deleteNotificationById(AlarmCategory alarmCategory, long commonId, long detailId) {

        notificationRepository.deleteNotificationCommonByDirectly(commonId);

        switch (alarmCategory) {
            case BILLING:
                notificationToBillingRepository.deleteNotificationToBillingByDirectly(detailId);
                break;
            case PARKING:
                notificationToParkingRepository.deleteNotificationToParkingByDirectly(detailId);
                break;
        }
    }

    @Override
    public void isAddNotificationCommonResponseDto(AlarmCategory alarmCategory) {

        isAddNotificationCommonSet.add(alarmCategory);
    }
}
