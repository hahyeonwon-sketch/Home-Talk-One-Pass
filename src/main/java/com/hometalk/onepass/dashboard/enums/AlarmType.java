package com.hometalk.onepass.dashboard.enums;

public enum AlarmType {

    // 관리비
    NEW("새로운 알림"),
    DUE_7D("7일 납부기한"),
    WARN_LONG("경고 알림"),

    // 주차
    REG_APPROVE("등록 승인"),
    REG_REJECT("등록 거절"),
    ENTRY_RES("입장 예약"),
    ENTRY_VIS("입장 방문"),
    OVER("시간 초과"),
    EXIT("출차");

//    REG_APPROVE, REG_REJECT, ENTRY_RES, ENTRY_VIS, OVER, EXIT

    private final String title;

    AlarmType(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }
}
