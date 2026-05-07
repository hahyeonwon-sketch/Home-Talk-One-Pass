package com.hometalk.onepass.dashboard.repository.notification;

import com.hometalk.onepass.auth.entity.User;
import com.hometalk.onepass.billing.entity.BillingStatus;
import com.hometalk.onepass.dashboard.entity.notification.NotificationToBilling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;


public interface NotificationToBillingRepository extends JpaRepository<NotificationToBilling, Long> {

    @Query("SELECT b FROM NotificationToBilling b " +
            "WHERE b.user.email = :email AND b.status = :status")
    List<NotificationToBilling> findByUserEmailAndStatus(
            @Param("email") String email,
            @Param("status") BillingStatus status
    );

    // 유저 이메일과 납부 상태로 리스트 검색
//    List<NotificationToBilling> findByUserEmailAndStatus(String email, BillingStatus status);
}
