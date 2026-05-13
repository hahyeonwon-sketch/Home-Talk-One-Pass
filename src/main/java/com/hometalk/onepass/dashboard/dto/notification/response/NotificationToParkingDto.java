package com.hometalk.onepass.dashboard.dto.notification.response;


import com.hometalk.onepass.dashboard.entity.notification.NotificationToParking;
import com.hometalk.onepass.dashboard.enums.AlarmCategory;
import com.hometalk.onepass.dashboard.enums.AlarmType;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class NotificationToParkingDto {

    private Long id;
    private AlarmCategory alarmCategory;      // 모듈별 세부 분류
    private AlarmType alarmType;
    private String vehicleNumber;      // 차량 번호 전체
    private String message;            // 메세지 내용
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;            // 삭제 시각

    /*  Entity --> DTO 변환 메서드 (정적 팩토리 메서드) */
    public static NotificationToParkingDto from(NotificationToParking notification) {
        return NotificationToParkingDto.builder()
                .id(notification.getId())
                .alarmCategory(notification.getAlarmCategory())
                .alarmType(notification.getAlarmType())
                .vehicleNumber(notification.getVehicleNumber())
                .message(notification.getMessage())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .deletedAt(notification.getDeletedAt())
                .build();
    }
}
