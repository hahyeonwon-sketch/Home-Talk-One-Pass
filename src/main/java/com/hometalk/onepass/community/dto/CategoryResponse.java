package com.hometalk.onepass.community.dto;

import com.hometalk.onepass.community.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryResponse {
    private Long id;
    private String name;
    private Long boardId;

    public CategoryResponse(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.boardId = category.getBoard().getId();
    }
}
