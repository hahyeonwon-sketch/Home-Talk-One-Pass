package com.hometalk.onepass.reservation.dto;

// import com.hometalk.onepass.reservation.entity.ReservationTime; <- start,end 한 번에 정의한 것 편하지만 꼬일 수 있음
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ReservationRequestDto {
    private Long facilityId;
    private String memberId;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
