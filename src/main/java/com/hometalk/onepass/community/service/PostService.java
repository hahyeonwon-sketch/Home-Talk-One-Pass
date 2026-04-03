package com.hometalk.onepass.community.service;

import com.hometalk.onepass.community.dto.PostRequestDTO;
import com.hometalk.onepass.community.dto.PostListResponse;
import com.hometalk.onepass.community.entity.Category;
import com.hometalk.onepass.community.entity.Post;
import com.hometalk.onepass.community.repository.CategoryRepository;
import com.hometalk.onepass.community.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    // Create
    @Transactional
    public Long postSave(PostRequestDTO dto) {
        Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(() ->
                    new IllegalArgumentException("해당 카테고리가 존재하지 않습니다." + dto.getCategoryId()));
        Post post = dto.toEntity(category);
        return postRepository.save(post).getId();
    }

    // Read
    public List<PostListResponse> postList(Long boardId, Long categoryId, int page) {
        List<Post> posts;
        if (categoryId == null) {
            // 게시판 전체 조회 (최신순)
            posts = postRepository.findAllByBoard_IdOrderByIdDesc(boardId);
        } else {
            // 특정 카테고리 조회 (최신순)
            posts = postRepository.findAllByBoard_IdAndCategory_IdOrderByIdDesc(boardId, categoryId);
        }

        return posts.stream().map(PostListResponse::new).toList();
    }
/*  필터링

    @Transactional
    public List<PostListResponse> getPostsByCondition(Long boardId, Long categoryId) {
        List<Post> posts;

        if (categoryId != null) {
            // 특정 카테고리 글만 조회
            posts = postRepository.findAllByCategoryIdOrderByIdDesc(categoryId);
        } else if (boardId != null) {
            // 특정 게시판의 모든 카테고리 글 조회
            posts = postRepository.findAllByCategoryBoardIdOrderByIdDesc(boardId);
        } else {
            // 전체 조회 (관리자용 등)
            posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        }

        return posts.stream()
                .map(PostListResponse::new)
                .collect(Collectors.toList());
    }*/

    // Read - 상세 페이지
    public Post postDetail(Long id) {
        return postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
    }

    // Update
    @Transactional
    public void postUpdate(Long id, PostRequestDTO dto) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
        post.update(dto);
    }
}
