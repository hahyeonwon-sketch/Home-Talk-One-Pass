package com.hometalk.onepass.inquiry.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Inquiry {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String memberId;

    @Column(nullable = false)
    private String title;

    private String category;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String content;

    private String answer;

    // 기본값을 '미답변'으로 설정
    @Builder.Default
    private String status = "미답변";

    private LocalDateTime createdAt;

    // DB에 처음 저장될 때 현재 시간을 자동으로 넣어줌
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 답변 등록 및 상태 변경 메소드
     */
    public void addAnswer(String answer) {
        this.answer = answer;
        this.status = "답변완료";
    }
}