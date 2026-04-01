package com.hometalk.onepass.reservation.dto;

import com.hometalk.onepass.reservation.entity.Reservation;
import com.hometalk.onepass.reservation.entity.ReservationStatus;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
public class ReservationResponseDto {
    private Long id;            // 예약 번호
    private String facilityName;        // 시설 이름
    private String memberId;        // 회원 ID
    private LocalDateTime startTime;    // 예약 시작 시간
    private LocalDateTime endTime;      // 예약 종료 시간
    private ReservationStatus status;   // 예약 상태

    // 엔티티를 DTO로 변환하는 메소드
    public static ReservationResponseDto fromEntity(Reservation reservation) {
        ReservationResponseDto dto = new ReservationResponseDto();
        dto.setId(reservation.getId());
        // 시설 객체에서 이름 빼오기
        dto.setFacilityName(reservation.getFacility().getName());
        dto.setMemberId(reservation.getMemberId());
        dto.setStartTime(reservation.getReservationTime().getStartTime());
        dto.setEndTime(reservation.getReservationTime().getEndTime());
        dto.setStatus(reservation.getStatus());
        return dto;
    }
}
