package com.hometalk.onepass.dashboard.repository.notification;

import com.hometalk.onepass.dashboard.entity.notification.NotificationCommon;
import com.hometalk.onepass.dashboard.enums.AlarmType;
import com.querydsl.core.types.OrderSpecifier;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import java.util.List;

// QClass 정적 임포트 (build 폴더 내에 생성된 파일)
import static com.hometalk.onepass.dashboard.entity.notification.QNotificationCommon.notificationCommon;

@RequiredArgsConstructor
public class NotificationRepositoryImpl implements NotificationCommonRepositoryCustom{

    private final JPAQueryFactory queryFactory;

    @Override
    public Page<NotificationCommon> findNotificationsByUrgent(boolean status, Pageable pageable) {

        List<NotificationCommon> content = queryFactory
                .selectFrom(notificationCommon)
                .where(notificationCommon.isRead.eq(status))
                .orderBy(createUrgentOrder()) // 커스텀 긴급순 정렬
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .fetch();

        Long total = queryFactory
                .select(notificationCommon.count())
                .from(notificationCommon)
                .where(notificationCommon.isRead.eq(status))
                .fetchOne();

        return new PageImpl<>(content, pageable, total != null ? total : 0L);
    }

    private OrderSpecifier<Integer> createUrgentOrder() {
        return new CaseBuilder()
                .when(notificationCommon.alarmType.eq(AlarmType.OVER)).then(1)
                .when(notificationCommon.alarmType.eq(AlarmType.WARN_LONG)).then(2)
                .when(notificationCommon.alarmType.eq(AlarmType.DUE_7D)).then(3)
                .when(notificationCommon.alarmType.eq(AlarmType.REG_REJECT)).then(4)
                .otherwise(5)
                .asc();
    }
}
