package com.hometalk.onepass.dashboard.repository.notification;


import com.hometalk.onepass.dashboard.entity.notification.UserAlarmConfig;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface NotificationReferenceIdRepository extends JpaRepository<UserAlarmConfig, Long> {

    Optional<UserAlarmConfig> findByUserId(Long userId);
}
