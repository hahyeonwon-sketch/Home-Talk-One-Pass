package com.hometalk.onepass.auth.repository;

import com.hometalk.onepass.auth.entity.User; // 임포트 확인
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // 이메일로 유저를 찾는 메서드 추가
    Optional<User> findByEmail(String email);
}
