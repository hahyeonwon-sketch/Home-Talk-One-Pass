package com.hometalk.onepass.dashboard.service.notification.impl;


import com.hometalk.onepass.dashboard.dto.notification.response.NotificationCommonResponseDto;
import com.hometalk.onepass.dashboard.entity.notification.NotificationCommon;
import com.hometalk.onepass.dashboard.repository.notification.NotificationRepository;
import com.hometalk.onepass.dashboard.service.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService{

    // 알림 관련 DB 접근을 담당하는 Repository
    private final NotificationRepository notificationRepository;

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

//    @Override
//    @Transactional(readOnly = true)   // 읽기 전용 트랜잭션 -> Hibernate 더티 체킹(변경 감지) 생략으로 성능 향상
//    public List<NotificationCommonResponseDto> findAllNotification(boolean isRead) {
//
//        List<NotificationCommon> notificationCommonList = new ArrayList<>();
//        for (NotificationCommon notificationCommon : notificationRepository.findAll()) {
//
//            if (notificationCommon.getIsRead() == isRead) {
//                notificationCommonList.add(notificationCommon);
//            }
//        }
//
//        return notificationCommonList
//                .stream()  // 스트림 변환
//                .map(NotificationCommonResponseDto::from)  // Entity -> DTO 변환 (LAZY 컬렉션 접근 없음)
//                .collect(Collectors.toList()); // 리스트로 수집 -> List<NotificationCommon>
//    }
}
