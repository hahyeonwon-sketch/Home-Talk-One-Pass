package com.hometalk.onepass.dashboard.repository.notification;

import com.hometalk.onepass.billing.entity.BillingStatus;
import com.hometalk.onepass.dashboard.entity.notification.NotificationToBilling;
import com.hometalk.onepass.dashboard.entity.notification.NotificationToParking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

// 알림 상세보기 저장소
public interface NotificationToParkingRepository extends JpaRepository<NotificationToParking, Long> {

    @Query("SELECT p FROM NotificationToParking p " +
            "WHERE p.user.email = :email")
    List<NotificationToParking> findByUserEmailAndStatus(@Param("email") String email);

    @Modifying
    @Query("DELETE FROM NotificationToParking n WHERE n.id = :id")
    void deleteNotificationToParkingByDirectly(@Param("id") Long id);
}
