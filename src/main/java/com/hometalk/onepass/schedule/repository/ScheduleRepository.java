package com.hometalk.onepass.schedule.repository;

import com.hometalk.onepass.schedule.entity.Schedule;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface ScheduleRepository extends JpaRepository<Schedule, Long> {
    // 특정 기간 내 일정 조회 (달력용)
    List<Schedule> findByStartAtBetween(LocalDateTime start, LocalDateTime end);
}