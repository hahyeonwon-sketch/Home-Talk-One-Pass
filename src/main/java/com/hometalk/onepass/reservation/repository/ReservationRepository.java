package com.hometalk.onepass.reservation.repository;

import com.hometalk.onepass.reservation.entity.Reservation;
import com.hometalk.onepass.reservation.entity.ReservationTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {

    // 본인이 이 시설을 이미 예약 했는지 체크
    boolean existsByFacilityIdAndMemberId(Long facilityId, String memberId);

    // 해당 시간에 이미 다른 예약이 있는지 체크
    boolean existsByFacilityIdAndReservationTime(Long facilityId, ReservationTime reservationTime);


}
