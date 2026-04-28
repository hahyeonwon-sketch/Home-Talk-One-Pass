package com.hometalk.onepass.community.dto.response;

import com.hometalk.onepass.community.entity.Post;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PostResponseDTO {
    private Long id;
    private String title;
    private String content;
    private String boardName;
    private Long categoryId;
    private String categoryName;
    private String categoryCode;
    private List<String> tags;
    private LocalDateTime createdAt;
    private int viewCount;
    private int commentCount;

    private String writer;
    private boolean editable;
    private boolean admin;
    private boolean pinned;
    private boolean isDeleted;

    // 1. 시스템 관리 상태
    private String postStatus;                  // 로직용 - "ACTIVE", "HIDDEN" (CSS 클래스나 조건문용)
    private String postStatusDescription;       // 표시용 - "활성", "숨김" (사용자 화면 출력용)

    // 2. 나눔 게시글 상태
    private String marketStatus;                // 로직용: "SHARED", "SOLD"
    private String marketStatusDescription;     // 표시용: "나눔중", "완료"

    // Entity -> DTO 변환 생성자
    public PostResponseDTO(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.content = post.getContent();
        this.pinned = post.isPinned();

        if (post.getCategory() != null) {
            this.categoryId = post.getCategory().getId();
            this.categoryName = post.getCategory().getName();
            this.categoryCode = post.getCategory().getCode();

            // 종속 관계를 안전하게 연결
            if (post.getCategory().getBoard() != null) {
                this.boardName = post.getCategory().getBoard().getName();
            }
        }

        this.writer = post.getWriter().getNickname();
        if (post.getPostTags() != null && !post.getPostTags().isEmpty()) {
            this.tags = post.getPostTags().stream()
                    .map(pt -> pt.getTag().getName())
                    .collect(Collectors.toList());
        } else {
            this.tags = new ArrayList<>(); // null 대신 빈 리스트
        }
        this.createdAt = post.getCreatedAt();
        this.isDeleted = (post.getDeletedAt() != null);
        this.viewCount = post.getViewCount();
        this.commentCount = post.getCommentCount();

        // 게시글 상태
        this.postStatus = post.getPostStatus().name();
        this.postStatusDescription = post.getPostStatus().getDescription();

        // 나눔 게시글 상태
        if (post.getMarketStatus() != null) {
            this.marketStatus = post.getMarketStatus().name();
            this.marketStatusDescription = post.getMarketStatus().getDescription();
        }

    }
}
