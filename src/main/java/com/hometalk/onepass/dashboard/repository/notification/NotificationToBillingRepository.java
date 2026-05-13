package com.hometalk.onepass.dashboard.repository.notification;

import com.hometalk.onepass.auth.entity.User;
import com.hometalk.onepass.billing.entity.BillingStatus;
import com.hometalk.onepass.dashboard.entity.notification.NotificationToBilling;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
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

    // 알림 삭제
    /*
        * cascade = CascadeType.ALL + fetch = FetchType.EAGER 환경에서
        기본 deleteById()는 영속성 켄텍스트 충돌 발생함.
        --> @Query()로 직접 DELETE SQL 실행하여 우회함
        * DELETE/UPDATE처럼 데이터를 변경하는 쿼리에는 반드시 @Modifying을 붙여야 함
     */
    @Modifying
    @Query("DELETE FROM NotificationToBilling n WHERE n.id = :id")
    void deleteNotificationToBillingByDirectly(@Param("id") Long id);

    // 유저 이메일과 납부 상태로 리스트 검색
//    List<NotificationToBilling> findByUserEmailAndStatus(String email, BillingStatus status);
}
