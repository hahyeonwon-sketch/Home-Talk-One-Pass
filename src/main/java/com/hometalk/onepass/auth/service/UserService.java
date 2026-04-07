package com.hometalk.onepass.auth.service;

import com.hometalk.onepass.auth.dto.SignUpDTO;
import com.hometalk.onepass.entity.auth.User;
import com.hometalk.onepass.auth.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class UserService implements UserRepository {

    public void resister(SignUpDTO dto) {
        User user = User.builder().name(dto.name).nickname().email().phoneNumber().status().role().build()
    }

}
