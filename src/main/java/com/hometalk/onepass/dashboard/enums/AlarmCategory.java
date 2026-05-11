package com.hometalk.onepass.dashboard.enums;

import lombok.Getter;

@Getter
public enum AlarmCategory {
    BILLING("관리비"),
    PARKING("주차"),
    NOTICE("공지사항"),
    SCHEDULE("일정관리"),
    COMMUNICATION("커뮤니케이션"),
    FACILITY("시설"),
    INQUIRY("민원"),
    RESERVATION("예약관리"),
    ALL("전체");

    private final String title;

    AlarmCategory(String title) {
        this.title = title;
    }

}
