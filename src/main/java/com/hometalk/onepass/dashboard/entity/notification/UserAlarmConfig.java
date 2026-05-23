package com.hometalk.onepass.dashboard.entity.notification;

import com.hometalk.onepass.dashboard.enums.AlarmCategory;
import com.hometalk.onepass.dashboard.service.notification.AlarmMapConverter;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
@Setter
@Entity
@Table(name = "user_alarm_config")
public class UserAlarmConfig {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", unique = true, nullable = false)
    private Long userId;

    @Setter
    @Convert(converter = AlarmMapConverter.class)
    @Column(columnDefinition = "TEXT") // 데이터가 길어질 수 있으므로 TEXT 타입 권장
    private Map<AlarmCategory, Set<Long>> sampleAlarmReferenceIdMap = new HashMap<>();


    // 비즈니스 로직에서 하나씩 안전하게 추가할 수 있도록 돕는 메서드
    public void addReferenceId(AlarmCategory category, Long referenceId) {
        this.sampleAlarmReferenceIdMap
                .computeIfAbsent(category, k -> new HashSet<>())
                .add(referenceId);
    }
}
