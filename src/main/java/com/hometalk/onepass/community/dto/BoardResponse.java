package com.hometalk.onepass.community.dto;

import com.hometalk.onepass.community.entity.Board;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BoardResponse {
    private Long id;
    private String name;

    public BoardResponse(Board board) {
        this.id = board.getId();
        this.name = board.getName();
    }
}
