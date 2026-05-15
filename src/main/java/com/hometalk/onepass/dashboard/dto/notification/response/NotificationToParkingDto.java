package com.hometalk.onepass.dashboard.dto.notification.response;


import com.hometalk.onepass.auth.entity.User;
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
    private User user;
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
                .user(notification.getUser())
                .vehicleNumber(notification.getVehicleNumber())
                .message(notification.getMessage())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .deletedAt(notification.getDeletedAt())
                .build();
    }

    /* DTO --> Entity 변환 메서드
     *   - Service 레이어에서 호출하여 Entity로 변환 후 Repository에 전달
     * */
    public NotificationToParking toEntity() {
        return NotificationToParking.builder()
                .alarmCategory(this.alarmCategory)
                .alarmType(this.alarmType)
                .user(this.user)
                .vehicleNumber(this.vehicleNumber)
                .message(this.message)
                .deletedAt(this.deletedAt)
                .build();
    }
}
