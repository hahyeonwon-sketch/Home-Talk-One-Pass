package com.hometalk.onepass.schedule.service;

import com.hometalk.onepass.auth.entity.LocalAccount;
import com.hometalk.onepass.auth.entity.User;
import com.hometalk.onepass.auth.repository.LocalAccountRepository;
import com.hometalk.onepass.notice.entity.Notice;
import com.hometalk.onepass.notice.repository.NoticeRepository;
import com.hometalk.onepass.schedule.dto.ScheduleCalResponseDto;
import com.hometalk.onepass.schedule.dto.ScheduleDetailResponseDto;
import com.hometalk.onepass.schedule.dto.ScheduleRequestDto;
import com.hometalk.onepass.schedule.entity.Schedule;
import com.hometalk.onepass.schedule.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;
    private final LocalAccountRepository localAccountRepository;
    private final NoticeRepository noticeRepository;

    // ── 현재 로그인 유저 가져오기 ─────────────────────────────────────────────
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        LocalAccount account = localAccountRepository.findByLoginId(auth.getName())
                .orElseThrow(() -> new RuntimeException("로그인 정보를 찾을 수 없습니다."));
        return account.getUser();
    }

    // ── 달력용 일정 목록 조회 (특정 년월) ────────────────────────────────────
    // 해당 월의 시작~끝 사이에 startAt이 있는 일정 반환
    @Transactional(readOnly = true)
    public List<ScheduleCalResponseDto> getSchedulesByMonth(int year, int month) {
        LocalDateTime start = LocalDateTime.of(year, month, 1, 0, 0);
        LocalDateTime end = start.plusMonths(1).minusSeconds(1);

        return scheduleRepository.findByStartAtBetween(start, end)
                .stream()
                .map(schedule -> new ScheduleCalResponseDto(
                        schedule.getId(),
                        schedule.getTitle(),
                        schedule.getStartAt(),
                        schedule.getEndAt(),
                        schedule.getNotice() != null ? schedule.getNotice().getId() : null,
                        schedule.getNotice() != null ? schedule.getNotice().getBadge().name() : null
                ))
                .collect(Collectors.toList());
    }

    // ── 일정 상세 조회 ────────────────────────────────────────────────────────
    @Transactional(readOnly = true)
    public ScheduleDetailResponseDto getScheduleDetail(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다. id: " + id));

        return new ScheduleDetailResponseDto(
                schedule.getId(),
                schedule.getNotice() != null ? schedule.getNotice().getId() : null,
                schedule.getTitle(),
                schedule.getInfo(),
                schedule.getLocation(),
                schedule.getReferenceUrl(),
                schedule.getStartAt(),
                schedule.getEndAt()
        );
    }

    // ── 일정 등록 ─────────────────────────────────────────────────────────────
    // noticeId가 있으면 공지와 연동, 없으면 독립 일정으로 등록
    public Long createSchedule(ScheduleRequestDto dto) {
        User user = getCurrentUser();

        // 공지 연동 (선택사항)
        Notice notice = null;
        if (dto.getNoticeId() != null) {
            notice = noticeRepository.findById(dto.getNoticeId())
                    .orElseThrow(() -> new RuntimeException("공지를 찾을 수 없습니다. id: " + dto.getNoticeId()));
        }

        Schedule schedule = Schedule.builder()
                .user(user)
                .notice(notice)
                .title(dto.getTitle())
                .info(dto.getInfo())
                .location(dto.getLocation())
                .referenceUrl(dto.getReferenceUrl())
                .startAt(dto.getStartAt())
                .endAt(dto.getEndAt())
                .build();

        scheduleRepository.save(schedule);
        return schedule.getId();
    }

    // ── 공지 작성 시 일정 자동 등록 ──────────────────────────────────────────
    // NoticeController에서 공지 저장 후 호출
    // scheduleName이 있을 때만 일정 저장 (선택사항)
    public void createScheduleWithNotice(Notice notice, String title,
                                         LocalDateTime startAt, LocalDateTime endAt,
                                         String info, String location, String referenceUrl) {
        if (title == null || title.isBlank()) return; // 일정명 없으면 저장 안 함
        if (startAt == null) return; // 시작시간 없으면 저장 안 함

        User user = getCurrentUser();

        Schedule schedule = Schedule.builder()
                .user(user)
                .notice(notice)
                .title(title)
                .info(info)
                .location(location)
                .referenceUrl(referenceUrl)
                .startAt(startAt)
                .endAt(endAt)
                .build();

        scheduleRepository.save(schedule);
    }

    // ── 일정 수정 (ADMIN만) ───────────────────────────────────────────────────
    public Long updateSchedule(Long id, ScheduleRequestDto dto) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다. id: " + id));

        schedule.update(
                dto.getTitle(),
                dto.getInfo(),
                dto.getLocation(),
                dto.getReferenceUrl(),
                dto.getStartAt(),
                dto.getEndAt()
        );
        return schedule.getId();
    }

    // ── 일정 삭제 (ADMIN만) ───────────────────────────────────────────────────
    public void deleteSchedule(Long id) {
        Schedule schedule = scheduleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("일정을 찾을 수 없습니다. id: " + id));
        scheduleRepository.delete(schedule);
    }
}