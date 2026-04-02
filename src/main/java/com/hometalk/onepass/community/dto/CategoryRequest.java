package com.hometalk.onepass.community.dto;

import com.hometalk.onepass.community.entity.Board;
import com.hometalk.onepass.community.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryRequest {
    private String name;
    private Long boardId;

    public Category toEntity(Board board) {
        Category category = new Category();
        category.setName(this.name);
        category.setBoard(board);
        return category;
    }
}
