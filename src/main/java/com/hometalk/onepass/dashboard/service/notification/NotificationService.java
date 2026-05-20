package com.hometalk.onepass.dashboard.service.notification;

import com.hometalk.onepass.auth.entity.User;
import com.hometalk.onepass.dashboard.dto.notification.response.NotificationCommonResponseDto;
import com.hometalk.onepass.dashboard.entity.notification.NotificationCommon;
import com.hometalk.onepass.dashboard.enums.AlarmCategory;
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

    Page<NotificationCommonResponseDto> findByIsAlarmCategory(boolean isRead, AlarmCategory alarmCategory, Pageable pageable);
    Page<NotificationCommonResponseDto> findNotificationsByUrgent(boolean status, Pageable pageable);

    NotificationCommonResponseDto  findNotificationCommonById(long id);
    Object findNotificationToDetailById(long id);

    User findUserByEmail(String email);
    User getCurrentUser();
    NotificationCommonResponseDto updateNotification(Long id, boolean isRead);
    void addNotification(AlarmCategory alarmCategory, Object object);
    void findNotificationByEmail(String email);
    void deleteNotificationById(AlarmCategory alarmCategory, long commonId, long detailId);
    void isAddNotificationCommonResponseDto(AlarmCategory alarmCategory);
    void sendAlarmSignal();
}