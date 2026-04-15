package com.hometalk.onepass.common.exception;

import lombok.Builder;
import lombok.Getter;
import java.time.LocalDateTime;

@Getter
@Builder
public class ErrorResponse {
    private final LocalDateTime timestamp = LocalDateTime.now();    // 에러 발생 시간
    private final int status;   // HTTP 상태 코드
    private final String error; // 상태 명
    private final String code;  // 비즈노스 로직에 따른 커스텀 에러 코드
    private final String message;   // 에러 메세지
}