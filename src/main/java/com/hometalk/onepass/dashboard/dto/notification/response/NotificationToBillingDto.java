package com.hometalk.onepass.dashboard.dto.notification.response;


import com.hometalk.onepass.auth.entity.User;
import com.hometalk.onepass.billing.dto.BillingDetailResponse;
import com.hometalk.onepass.billing.entity.BillingStatus;
import com.hometalk.onepass.dashboard.entity.notification.NotificationToBilling;
import com.hometalk.onepass.dashboard.enums.AlarmCategory;
import com.hometalk.onepass.dashboard.enums.AlarmType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.JoinColumn;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
public class NotificationToBillingDto {

    private Long id;
    private AlarmCategory alarmCategory;    // 알림 발생 모듈
    private AlarmType alarmType;            // 모듈별 세부 분류
    private User user;
    private String message;                 // 메세지 내용
    private String billingMonth;
    private BigDecimal totalAmount;         // 합계 금액
    private BillingStatus status;
    private LocalDate dueDate;              // 납기일
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;        // 삭제 시각

    private final List<BillingDetailResponse.ItemDetail> billingItems;

    /*  Entity --> DTO 변환 메서드 (정적 팩토리 메서드) */
    public static NotificationToBillingDto from(NotificationToBilling notification) {
        return NotificationToBillingDto.builder()
                .id(notification.getId())
                .alarmCategory(notification.getAlarmCategory())
                .alarmType(notification.getAlarmType())
                .user(notification.getUser())
                .billingItems(notification.getBillingItems().stream()
                        .map(d -> BillingDetailResponse.ItemDetail.builder()
                                .itemName(d.getItemName())
                                .itemAmount(d.getItemAmount())
                                .build())
                        .collect(Collectors.toList()))
                .message(notification.getMessage())
                .billingMonth(notification.getBillingMonth())
                .totalAmount(notification.getTotalAmount())
                .status(notification.getStatus())
                .dueDate(notification.getDueDate())
                .createdAt(notification.getCreatedAt())
                .updatedAt(notification.getUpdatedAt())
                .deletedAt(notification.getDeletedAt())
                .build();
    }

    /* DTO --> Entity 변환 메서드
     *   - Service 레이어에서 호출하여 Entity로 변환 후 Repository에 전달
     * */
    public NotificationToBilling toEntity() {
        return NotificationToBilling.builder()
                .alarmCategory(this.alarmCategory)
                .alarmType(this.alarmType)
                .user(this.user)
                .billingItems(this.billingItems)
                .message(this.message)
                .billingMonth(this.billingMonth)
                .totalAmount(this.totalAmount)
                .status(this.status)
                .dueDate(this.dueDate)
                .deletedAt(this.deletedAt)
                .build();
    }
}
