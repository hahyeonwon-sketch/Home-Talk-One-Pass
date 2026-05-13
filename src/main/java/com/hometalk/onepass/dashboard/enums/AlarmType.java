package com.hometalk.onepass.dashboard.enums;

import lombok.Getter;

@Getter
public enum AlarmType {

    // 🚨 1단계: 가장 긴급 (주차 차단, 시간 초과 등 즉시 조치 필요)
    OVER("시간 초과", 1),
    WARN_LONG("경고 알림", 2),

    // ⚠️ 2단계: 중간 긴급 (기한 마감, 등록 거절 등)
    DUE_7D("7일 납부기한", 3),
    REG_REJECT("등록 거절", 4),

    // ℹ️ 3단계: 일반/안내 (승인, 예약, 신규 알림 등)
    REG_APPROVE("등록 승인", 5),
    ENTRY_RES("입장 예약", 6),
    ENTRY_VIS("입장 방문", 7),
    EXIT("출차", 8),
    NEW("새로운 알림", 9);

//    REG_APPROVE, REG_REJECT, ENTRY_RES, ENTRY_VIS, OVER, EXIT

    private final String title;
    private final int priority; // 긴급도 순위 (1이 가장 높음)

    AlarmType(String title,  int priority) {
        this.title = title;
        this.priority = priority;
    }
}
