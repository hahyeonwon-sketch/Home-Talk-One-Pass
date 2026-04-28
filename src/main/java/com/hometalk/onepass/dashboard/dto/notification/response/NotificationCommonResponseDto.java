package com.hometalk.onepass.dashboard.dto.notification.response;

import com.hometalk.onepass.dashboard.entity.notification.NotificationCommon;
import com.hometalk.onepass.dashboard.enums.AlarmCategory;
import com.hometalk.onepass.dashboard.enums.AlarmType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationCommonResponseDto {

    private Long id;
    private AlarmCategory alarmCategory;         // 알림 발생 카테고리
    private AlarmType alarmType;      // 알림 타입 분류
    private Boolean isRead;            // 읽음 여부 상태
    private String message;            // 메세지 내용
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;            // 삭제 시각

    /*  Entity --> DTO 변환 메서드 (정적 팩토리 메서드) */
    public static NotificationCommonResponseDto from(NotificationCommon notification) {
        return NotificationCommonResponseDto.builder()
                .id(notification.getId())
                .alarmCategory(notification.getAlarmCategory())
                .alarmType(notification.getAlarmType())
                .isRead(notification.getIsRead())
                .message(notification.getMessage())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .deletedAt(notification.getDeletedAt())
                .build();
    }
}
