package com.hometalk.onepass.community.dto;

import com.hometalk.onepass.community.entity.Post;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class PostListResponse {
    private Long id;
    private String title;
    private boolean pinned;
    private String boardId;
    private String categoryId;
    //private String writerNickname;
    private LocalDateTime createdAt;
    private int viewCount;
    private int commentCount;

    public PostListResponse(Post post) {
        this.id = post.getId();
        this.title = post.getTitle();
        this.pinned = post.isPinned();
        this.boardId = post.getCategory().getBoard().getName();
        this.categoryId = post.getCategory().getName();
        //this.writerNickname = post.getWriter().getNickname();
        this.createdAt = post.getCreatedAt();
        this.viewCount = post.getViewCount();
        this.commentCount = post.getCommentCount();
    }
}
