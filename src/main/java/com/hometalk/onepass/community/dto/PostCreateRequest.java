package com.hometalk.onepass.community.dto;

import com.hometalk.onepass.community.entity.Category;
import com.hometalk.onepass.community.entity.Post;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PostCreateRequest {
    private String title;
    private String content;
    private Long writerId;      // Member Entity 구현 전까지 유지
    private Long categoryId;
    private boolean pinned;

    public Post toEntity(Category category) {
        Post post = new Post();
        post.setTitle(this.title);
        post.setContent(this.content);
        post.setPinned(this.pinned);
        post.setWriterId(this.writerId);
        post.setCategory(category);
        return post;
    }
}
