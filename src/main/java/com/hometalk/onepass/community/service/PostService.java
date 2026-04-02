package com.hometalk.onepass.community.service;

import com.hometalk.onepass.community.dto.PostCreateRequest;
import com.hometalk.onepass.community.dto.PostListResponse;
import com.hometalk.onepass.community.dto.PostUpdateRequest;
import com.hometalk.onepass.community.entity.Category;
import com.hometalk.onepass.community.entity.Post;
import com.hometalk.onepass.community.repository.CategoryRepository;
import com.hometalk.onepass.community.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PostService {
    private final PostRepository postRepository;
    private final CategoryRepository categoryRepository;

    // Create
    public Long postSave(PostCreateRequest dto) {
        Category category = categoryRepository.findById(dto.getCategoryId()).orElseThrow(() ->
                    new IllegalArgumentException("해당 카테고리가 존재하지 않습니다." + dto.getCategoryId()));
        Post post = dto.toEntity(category);
        return postRepository.save(post).getId();
    }

    // Read
    public List<PostListResponse> postList(Long boardId, Long categoryId) {
        // 최신순 정렬
        List<Post> posts = postRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return posts.stream().map(PostListResponse::new).collect(Collectors.toList());
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
    public void postUpdate(Long id, PostUpdateRequest dto) {
        Post post = postRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("게시글이 없습니다."));
        post.update(dto);
    }
}
