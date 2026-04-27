package com.hometalk.onepass.notice.repository;

import com.hometalk.onepass.notice.entity.ReadLog;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReadLogRepository extends JpaRepository<ReadLog, Long> {
    boolean existsByUserIdAndNoticeId(Long userId, Long noticeId);
    List<Long> findNoticeIdByUserId(Long userId);
}