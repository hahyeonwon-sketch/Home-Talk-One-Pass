package com.hometalk.onepass.community.service;

import com.hometalk.onepass.community.dto.BoardResponse;
import com.hometalk.onepass.community.entity.Board;
import com.hometalk.onepass.community.entity.Post;
import com.hometalk.onepass.community.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BoardService {
    private final BoardRepository boardRepository;

    public List<BoardResponse> findAll() {
        return boardRepository.findAll().stream()
            .map(board -> new BoardResponse(board))
            .collect(Collectors.toList());
    }

    public BoardResponse findById(Long id) {
        return boardRepository.findById(id).map(board -> new BoardResponse(board)).orElse(null);
    }
}
