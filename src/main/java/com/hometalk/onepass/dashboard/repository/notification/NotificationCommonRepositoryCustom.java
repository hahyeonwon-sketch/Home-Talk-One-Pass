package com.hometalk.onepass.dashboard.repository.notification;

import com.hometalk.onepass.dashboard.dto.notification.response.NotificationCommonResponseDto;
import com.hometalk.onepass.dashboard.entity.notification.NotificationCommon;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface NotificationCommonRepositoryCustom {

    Page<NotificationCommon> findNotificationsByUrgent(boolean status, Pageable pageable);
}
