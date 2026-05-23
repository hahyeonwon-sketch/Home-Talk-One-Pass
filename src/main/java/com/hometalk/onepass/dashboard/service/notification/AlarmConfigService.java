package com.hometalk.onepass.dashboard.service.notification;

import com.hometalk.onepass.dashboard.entity.notification.UserAlarmConfig;
import com.hometalk.onepass.dashboard.enums.AlarmCategory;
import com.hometalk.onepass.dashboard.repository.notification.NotificationReferenceIdRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@RequiredArgsConstructor
public class AlarmConfigService {

    // 알림 레퍼런스 저장 DB
    private final NotificationReferenceIdRepository notificationReferenceIdRepository;

    @Transactional
    public void saveConfigAll(Long userId, Map<AlarmCategory, Set<Long>> map) {

        UserAlarmConfig config = notificationReferenceIdRepository.findById(userId)
                .orElseGet(() -> {
                    UserAlarmConfig newConfig = new UserAlarmConfig();
                    newConfig.setUserId(userId);
                    return newConfig;
                });

        config.setSampleAlarmReferenceIdMap(map);
        notificationReferenceIdRepository.save(config);
    }

    @Transactional
    public void saveConfigOne(Long userId, AlarmCategory category, Long referenceId) {

        UserAlarmConfig config = notificationReferenceIdRepository.findById(userId)
                .orElseGet(() -> {
                    UserAlarmConfig newConfig = new UserAlarmConfig();
                    newConfig.setUserId(userId);
                    return newConfig;
                });

        config.addReferenceId(category, referenceId);
        notificationReferenceIdRepository.save(config);
    }

    @Transactional(readOnly = true)
    public List<UserAlarmConfig> getAllAlarmConfigs() {
        // DB에 저장된 user_alarm_config 테이블의 모든 데이터를 List로 가져옵니다.
        // 이때 내부의 각 Map 데이터들은 컨버터(@Convert)에 의해 자동으로 자바 Map 객체로 복원되어 채워집니다.
        return notificationReferenceIdRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Map<AlarmCategory, Set<Long>> getCombinedAlarmMap() {

        List<UserAlarmConfig> configs = getAllAlarmConfigs();
        Map<AlarmCategory, Set<Long>> combinedMap = new HashMap<>();


        for (UserAlarmConfig config : configs) {
            Map<AlarmCategory, Set<Long>> targetMap = config.getSampleAlarmReferenceIdMap();
            if (targetMap == null) continue;


            for (Map.Entry<AlarmCategory, Set<Long>> entry : targetMap.entrySet()) {
                AlarmCategory category = entry.getKey();
                Set<Long> ids = entry.getValue();

                if (ids != null) {
                    // 이미 결과 맵에 해당 카테고리가 있으면 기존 Set에 더하고, 없으면 새로 HashSet을 생성하여 누적
                    combinedMap.computeIfAbsent(category, k -> new HashSet<>()).addAll(ids);
                }
            }
        }

        return combinedMap; // 최종 병합된 하나의 Map 반환
    }
}
