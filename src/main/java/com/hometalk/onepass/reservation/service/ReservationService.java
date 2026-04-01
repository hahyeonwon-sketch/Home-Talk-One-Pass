package com.hometalk.onepass.reservation.service;

import com.hometalk.onepass.facility.entity.Facility;
import com.hometalk.onepass.facility.repository.FacilityRepository;
import com.hometalk.onepass.reservation.dto.ReservationRequestDto;
import com.hometalk.onepass.reservation.dto.ReservationResponseDto;
import com.hometalk.onepass.reservation.entity.Reservation;
import com.hometalk.onepass.reservation.entity.ReservationStatus;
import com.hometalk.onepass.reservation.entity.ReservationTime;
import com.hometalk.onepass.reservation.repository.ReservationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReservationService  {
    private final FacilityRepository facilityRepository;
    private final ReservationRepository reservationRepository;

    /*
        시설 예약 등록
     */
    @Transactional
    public Long register(Reservation reservation) {
        // 이미 예약된 시설인지 확인
        boolean existsMember = reservationRepository.existsByFacilityIdAndMemberId(
                reservation.getFacility().getId(),
                reservation.getMemberId());
        if (existsMember) {
            throw new RuntimeException("이미 이 시설에 대한 예약 내역이 존재합니다.");
        }
        // 해당 시간에 다른 예약자가 있는지 확인
        boolean existsTime = reservationRepository.existsByFacilityIdAndReservationTime(
                reservation.getFacility().getId(),
                reservation.getReservationTime());

        if (existsTime) {
            throw new RuntimeException("해당 시간에 다른 예약자가 있습니다.");
        }

        return reservationRepository.save(reservation).getId();
    }

    public Reservation findOne(Long id) {
        return reservationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("해당 예약을 찾을 수 없습니다."));
    }

    /*
        모든 예약 내역 조회
     */
    public List<ReservationResponseDto> findAll() {
        return reservationRepository.findAll().stream()
                .map(ReservationResponseDto::fromEntity)
                .toList();
    }

    /*
        예약 취소
     */
    @Transactional
    public void cancel(Long id) {
        Reservation reservation = findOne(id);
        reservation.cancel();
    }

    // 등록 (dto)
    @Transactional
    public Long register(ReservationRequestDto dto) {
        Facility facility = facilityRepository.findById(dto.getFacilityId())
                    .orElseThrow(() -> new RuntimeException("해당 시설을 찾을 수 없습니다."));
        Reservation reservation = Reservation.builder()
                .facility(facility)
                .memberId(dto.getMemberId())
                .reservationTime(new ReservationTime(dto.getStartTime(), dto.getEndTime()))
                .status(ReservationStatus.COMPLETED)
                .build();
                return reservationRepository.save(reservation).getId();
    }


}
