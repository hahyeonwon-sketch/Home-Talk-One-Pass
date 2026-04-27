package com.hometalk.onepass.dashboard.enums;

public enum AlarmBillingType {
    NEW("새로운 알림"),
    DUE_7D("7일 납부기한"),
    WARN_LONG("경고 알림");

    private final String title;

    AlarmBillingType(String title) {
        this.title = title;
    }
}
