package com.hometalk.onepass.dashboard.service.notification.impl;


import com.hometalk.onepass.auth.entity.User;
import com.hometalk.onepass.auth.repository.UserRepository;
import com.hometalk.onepass.billing.entity.BillingStatus;
import com.hometalk.onepass.dashboard.controller.NotificationApiController;
import com.hometalk.onepass.dashboard.dto.notification.response.NotificationCommonResponseDto;
import com.hometalk.onepass.dashboard.dto.notification.response.NotificationToBillingDto;
import com.hometalk.onepass.dashboard.dto.notification.response.NotificationToParkingDto;
import com.hometalk.onepass.dashboard.entity.notification.NotificationCommon;
import com.hometalk.onepass.dashboard.entity.notification.NotificationToBilling;
import com.hometalk.onepass.dashboard.entity.notification.NotificationToParking;
import com.hometalk.onepass.dashboard.enums.AlarmCategory;
import com.hometalk.onepass.dashboard.repository.notification.NotificationReferenceIdRepository;
import com.hometalk.onepass.dashboard.repository.notification.NotificationRepository;
import com.hometalk.onepass.dashboard.repository.notification.NotificationToBillingRepository;
import com.hometalk.onepass.dashboard.repository.notification.NotificationToParkingRepository;
import com.hometalk.onepass.dashboard.service.notification.AlarmConfigService;
import com.hometalk.onepass.dashboard.service.notification.NotificationService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;


import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{


    // 유저 관련 DB
    private final UserRepository userRepository;
    // 알림 저장 서비스
    private final AlarmConfigService alarmConfigService;

    // 알림 관련 DB 접근을 담당하는 Repository
    private final NotificationRepository notificationRepository;
    private final NotificationToBillingRepository notificationToBillingRepository;
    private final NotificationToParkingRepository notificationToParkingRepository;

    // 공통 DB 추가 가능 여부
    private final Set<AlarmCategory> isAddNotificationCommonSet = new HashSet<>();

    // 레퍼런스 아뒤 리스트
    Map<AlarmCategory, Set<Long>> sampleAlarmReferenceIdMap = new HashMap<>();

    private User currentUser = null;        // 현재 유저
    private boolean alarmBadge = false;     // 알림 표시에 사용할 변수
    private boolean createAlarmReferenceId = false;   // 알림 상세보기에 사용할 레퍼런스 아뒤

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

        Page<NotificationCommon> notificationCommonsList = notificationRepository.findByIsRead(false, pageable);
        return notificationCommonsList.map(NotificationCommonResponseDto::from);    // Page<NotificationCommonResponseDto> -> Page<NotificationCommonResponseDto> 변환 (메타정보 유지)
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

        Page<NotificationCommon> notificationCommonsList = notificationRepository.findByIsRead(true, pageable);
        return notificationCommonsList.map(NotificationCommonResponseDto::from);
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
    public User findUserByEmail(String email) {

        assert userRepository != null;
        currentUser = userRepository.findByEmail(email)
                .orElseGet(() -> {
                    // 2. 만약 없다면, 필수 필드를 모두 채워서 저장합니다.
                    return userRepository.save(User.builder()
                            .name("테스트유저")
                            .email(email)
                            .nickname("테스트닉네임")
                            .phoneNumber("010-0000-0000") // 필수값들
                            .role(User.UserRole.MEMBER)      // Enum 값들
                            .status(User.UserStatus.APPROVED)
                            .build());
                });

        log.info("currentUser.getEmail == {}", currentUser.getEmail());
        return currentUser;
    }

    @Override
    public NotificationCommonResponseDto updateNotification(Long id, boolean isRead) {

        // 1. 기존 데이터를 DB에서 조회 (영속화)
        NotificationCommon entity = notificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("해당 알림이 없습니다."));

        if (isRead) {

            if (entity.getIsRead() == false) {

                entity.setIsRead(true);
                notificationRepository.save(entity);
            }
        }

        return NotificationCommonResponseDto.from(entity);
    }

    @Override
    public void addNotification(AlarmCategory category, Object object) {

        // 1. 해당 카테고리의 Set이 이미 Map에 있는지 확인
        if (!sampleAlarmReferenceIdMap.containsKey(category)) {
            // 2. 없다면 새 HashSet을 생성하여 Map에 먼저 넣기
            sampleAlarmReferenceIdMap.put(category, new HashSet<>());
        }

        boolean isAdd = false;
        switch (category) {
            case BILLING:

                NotificationToBillingDto notificationToBillingDto = (NotificationToBillingDto) object;
                NotificationToBilling notificationToBilling =
                        notificationToBillingRepository.save(notificationToBillingDto.toEntity());

                isAdd = sampleAlarmReferenceIdMap.get(category).add(notificationToBilling.getId());
                if (isAdd) {

                    NotificationCommon notificationCommon = NotificationCommon.builder()
                            .alarmCategory(notificationToBilling.getAlarmCategory())
                            .alarmType(notificationToBilling.getAlarmType())
                            .referenceId(notificationToBilling.getId())
                            .message(notificationToBilling.getMessage())
                            .user(notificationToBilling.getUser())
                            .isRead(false)
                            .build();

                    alarmConfigService.saveConfigOne(currentUser.getId(), category, notificationToBilling.getId());
                    notificationRepository.save(notificationCommon);
                    log.info("샘플 관리비 알람 삽입 완료.");
                }
                else {

                    notificationToBillingRepository.deleteById(notificationToBilling.getId());
                    log.info("샘플 관리비 알람이 이미 존재합니다..");
                }
                break;
            case PARKING:

                NotificationToParkingDto notificationToParkingDto = (NotificationToParkingDto) object;
                NotificationToParking notificationToParking =
                        notificationToParkingRepository.save(notificationToParkingDto.toEntity());

                isAdd = sampleAlarmReferenceIdMap.get(category).add(notificationToParking.getId());
                if (isAdd) {

                    NotificationCommon notificationCommon = NotificationCommon.builder()
                            .alarmCategory(notificationToParking.getAlarmCategory())
                            .alarmType(notificationToParking.getAlarmType())
                            .referenceId(notificationToParking.getId())
                            .message(notificationToParking.getMessage())
                            .user(notificationToParking.getUser())
                            .isRead(false)
                            .build();

                    alarmConfigService.saveConfigOne(currentUser.getId(), category, notificationToParking.getId());
                    notificationRepository.save(notificationCommon);
                    log.info("샘플 주차 알람 삽입 완료.");
                }
                else {

                    notificationToParkingRepository.deleteById(notificationToParking.getId());
                    log.info("샘플 주차 알람이 이미 존재합니다..");
                }
                break;
        }
    }

    @Override
    public void findNotificationByEmail() {

        List<NotificationCommon> sampleAlarmList = new ArrayList<>();
        boolean isAddBilling = isAddNotificationCommonSet.contains(AlarmCategory.BILLING);
        boolean isAddParking = isAddNotificationCommonSet.contains(AlarmCategory.PARKING);

        log.info("샘플 관리비 공통 현재 갯수 = {}", notificationToBillingRepository.count());
        if (isAddBilling) {

            AlarmCategory category = AlarmCategory.BILLING;

            // 1. 해당 카테고리의 Set이 이미 Map에 있는지 확인
            if (!sampleAlarmReferenceIdMap.containsKey(category)) {
                // 2. 없다면 새 HashSet을 생성하여 Map에 먼저 넣기
                sampleAlarmReferenceIdMap.put(category, new HashSet<>());
            }

            List<NotificationToBilling> unpaidBillingList =
                    notificationToBillingRepository.findByUserEmailAndStatus(currentUser.getEmail(), BillingStatus.UNPAID);

            boolean isAdd = false;
            for (NotificationToBilling notificationToBilling : unpaidBillingList) {

                isAdd = sampleAlarmReferenceIdMap.get(category).add(notificationToBilling.getId());
                if (isAdd) {

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
        }
        else {
            // 이미 데이터가 있으면 중복 삽입하지 않음
            log.info("[DataInitializer]이미 관리비 공통 데이터가 존재합니다. 시드 데이터 삽입을 건너뜁니다.");
        }

        log.info("샘플 주차 공통 현재 갯수 = {}", notificationToParkingRepository.count());
        if (isAddParking) {

            AlarmCategory category = AlarmCategory.PARKING;

            // 1. 해당 카테고리의 Set이 이미 Map에 있는지 확인
            if (!sampleAlarmReferenceIdMap.containsKey(category)) {
                // 2. 없다면 새 HashSet을 생성하여 Map에 먼저 넣기
                sampleAlarmReferenceIdMap.put(category, new HashSet<>());
            }

            List<NotificationToParking> parkingList =
                    notificationToParkingRepository.findByUserEmailAndStatus(currentUser.getEmail());

            boolean isAdd = false;
            for (NotificationToParking notificationToParking : parkingList) {

                isAdd = sampleAlarmReferenceIdMap.get(category).add(notificationToParking.getId());
                if (isAdd) {

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
        }
        else {
            // 이미 데이터가 있으면 중복 삽입하지 않음
            log.info("[DataInitializer]이미 주차 공통 데이터가 존재합니다. 시드 데이터 삽입을 건너뜁니다.");
        }


        if (isAddBilling || isAddParking) {
            isAddNotificationCommonSet.clear();
            notificationRepository.saveAll(sampleAlarmList);
            alarmConfigService.saveConfigAll(currentUser.getId(), sampleAlarmReferenceIdMap);
            log.info("샘플 공통 알람 {}건 삽입 완료.", sampleAlarmList.size());
        }
    }

    @Override
    @Transactional
    public void deleteNotificationById(AlarmCategory alarmCategory, long commonId, long detailId) {

        Set<Long> idSet = sampleAlarmReferenceIdMap.get(alarmCategory);

        if (idSet == null) {
            throw new IllegalArgumentException("삭제 실패: 존재하지 않는 알림 카테고리입니다. Category = " + alarmCategory);
        }

        // 메모리에서 ID 제거 시도 및 결과 확인
        if (!idSet.remove(detailId)) {
            throw new IllegalArgumentException("삭제 실패: 해당 알림 ID가 메모리에 존재하지 않습니다. ID = " + detailId);
        }

        if (idSet.isEmpty()) {
            sampleAlarmReferenceIdMap.remove(alarmCategory); // 맵에서 카테고리 키 자체를 삭제하여 메모리 방지
        }

        alarmConfigService.deletePartialReferenceIdIds(currentUser.getId(), alarmCategory, detailId);

        try {

            notificationRepository.deleteNotificationCommonByDirectly(commonId);


            switch (alarmCategory) {
                case BILLING:
                    notificationToBillingRepository.deleteNotificationToBillingByDirectly(detailId);
                    break;
                case PARKING:
                    notificationToParkingRepository.deleteNotificationToParkingByDirectly(detailId);
                    break;
                default:
                    log.warn("정의되지 않은 알람 카테고리 DB 삭제 요청: {}", alarmCategory);
                    break;
            }

            log.info("성공적으로 알림을 삭제했습니다. CommonID: {}, DetailID: ({})", commonId, detailId);
        } catch (Exception e) {
            // DB 삭제 중 예외 발생 시 로그를 남기고 런타임 예외를 다시 던져 트랜잭션 롤백을 유도합니다.
            log.error("알림 DB 삭제 중 오류 발생: {}", e.getMessage());
            throw new RuntimeException("알림 데이터베이스 삭제 작업 중 오류가 발생했습니다.", e);
        }
    }

    @Override
    public void isAddNotificationCommonResponseDto(AlarmCategory alarmCategory) {

        isAddNotificationCommonSet.add(alarmCategory);
    }

    @Override
    public void sendAlarmSignal() {
        SseEmitter emitter = NotificationApiController.emitters.get(currentUser.getEmail());

        if (emitter != null) {
            try {

                log.info("send alarm signal");

                alarmBadge = notificationRepository.existsByIsRead(false);
                if (alarmBadge) {

                    emitter.send(SseEmitter.event()
                            .name("alarm-signal")    // 프론트의 addEventListener 명칭과 매칭
                            .data("NEW_ALARM"));    // 단순히 알림이 왔다는 신호 데이터만 전송
                }
                else {

                    emitter.send(SseEmitter.event()
                            .name("alarm-signal")
                            .data("AllRead_ALARM"));        // 알림 표시를 지운다.
                }
            } catch (IOException e) {
                NotificationApiController.emitters.remove(currentUser.getEmail());
                log.error("실패: 전송 중 오류 발생 : {}", e.getMessage());
            }
        }
        else {
            log.warn("emitter == null : 알림 표시가 전송 되지 않았습니다.");
        }
    }

    @Override
    public boolean getAlarmBadge() {
        return alarmBadge;
    }

    @Override
    public void addListAlarmReferenceId() {

        if (!createAlarmReferenceId) {

            createAlarmReferenceId = true;
            sampleAlarmReferenceIdMap = alarmConfigService.getCombinedAlarmMap();
//            log.info("sampleAlarmReferenceIdMap.size() = {}", sampleAlarmReferenceIdMap.size());
//
//            for (Map.Entry<AlarmCategory, Set<Long>> entry : sampleAlarmReferenceIdMap.entrySet()) {
//
//                log.info("sampleAlarmReferenceIdMap.getKey = {}", entry.getKey());
//                log.info("sampleAlarmReferenceIdMap.getValue = {}", entry.getValue());
//            }
        }
    }

    @Override
    public User getCurrentUser() {
        return currentUser;
    }
}
