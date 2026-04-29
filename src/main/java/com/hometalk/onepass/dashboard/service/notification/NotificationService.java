package com.hometalk.onepass.dashboard.service.notification;

import com.hometalk.onepass.dashboard.dto.notification.response.NotificationCommonResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface NotificationService {

    // isRead 필드가 false(읽지 않음)인 데이터만 조회
    List<NotificationCommonResponseDto> findByIsNotReadNotification();
    Page<NotificationCommonResponseDto> findByIsNotReadNotification(Pageable pageable);

    // 읽은(True) 데이터 + 최신순 (추가)
    List<NotificationCommonResponseDto> findByIsReadNotification();
    Page<NotificationCommonResponseDto> findByIsReadNotification(Pageable pageable);
}