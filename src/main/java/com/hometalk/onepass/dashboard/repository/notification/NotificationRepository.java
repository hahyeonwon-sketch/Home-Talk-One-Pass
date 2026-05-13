package com.hometalk.onepass.dashboard.repository.notification;

import com.hometalk.onepass.dashboard.entity.notification.NotificationCommon;
import com.hometalk.onepass.dashboard.enums.AlarmCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/*
 *   공통 알림 DB 접근을 담당하는 Repository
 *   알림 엔티티, PK 타입 Long
 *   - 기본 제공 메서드 : save() -> INSERT / UPDATE, findeAll() -> SELECT, findByid() -> SELECT by PK, delete() 등
 *   - 커스텀 쿼리가 필요하면 @Query 추가, 명명 규칙 등
 * */

public interface NotificationRepository extends JpaRepository<NotificationCommon, Long>, NotificationCommonRepositoryCustom {

    // JpaRepository의 기본 메서드(save, findById, findAll 등)와
    // Custom 인터페이스에 선언한 Querydsl 메서드(findNotificationsByUrgent)를 모두 사용할 수 있게 됩니다.

    // boolean 값(status)을 인자로 받아 검색
    List<NotificationCommon> findByIsRead(Boolean status);
    Page<NotificationCommon> findByIsRead(Boolean status, Pageable pageable);


    @Query("SELECT n FROM NotificationCommon n WHERE n.isRead = :status AND n.alarmCategory = :alarmCategory")
    Page<NotificationCommon> findByIsReadAndAlarmCategory(Boolean status, AlarmCategory alarmCategory, Pageable pageable);

    @Modifying
    @Query("DELETE FROM NotificationCommon n WHERE n.id = :id")
    void deleteNotificationCommonByDirectly(@Param("id") Long id);
}