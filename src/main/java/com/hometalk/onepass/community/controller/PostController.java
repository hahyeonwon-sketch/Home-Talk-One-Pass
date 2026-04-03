package com.hometalk.onepass.community.controller;

import com.hometalk.onepass.community.dto.*;
import com.hometalk.onepass.community.entity.Post;
import com.hometalk.onepass.community.service.BoardService;
import com.hometalk.onepass.community.service.CategoryService;
import com.hometalk.onepass.community.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/community")
public class PostController {
    private final PostService postService;
    private final BoardService boardService;
    private final CategoryService categoryService;

    // 게시판 목록
    // 게시판별 메인 (카테고리 '전체' 상태)
    @GetMapping("/{boardCode}")
    public String boardMain(@PathVariable String boardCode,
                            @RequestParam(defaultValue = "0") int page,
                            Model model) {
        // 엔티티가 아닌 DTO를 받음
        BoardResponseDTO board = boardService.findByCode(boardCode);
        return fillCommunityModel(board, null, page, model);
    }

    @GetMapping("/{boardCode}/{categoryCode}")
    public String categoryList(@PathVariable String boardCode,
                               @PathVariable String categoryCode,
                               @RequestParam(defaultValue = "0") int page,
                               Model model) {
        BoardResponseDTO board = boardService.findByCode(boardCode);
        CategoryResponseDTO category = categoryService.findByCode(categoryCode);

        return fillCommunityModel(board, category, page, model);
    }

    // 게시글 상세 페이지
    @GetMapping("/{boardCode}/{id}")
    public String postDetail(@PathVariable Long id, Model model) {
        Post post = postService.postDetail(id);
        model.addAttribute("post", new PostResponseDTO(post));
        return "community/postDetail";
    }

    // 게시글 작성 폼
    @GetMapping("/{boardCode}/write")
    public String postForm(Model model) {
        model.addAttribute("post", new PostRequestDTO());
        return "community/postForm";
    }

    // 게시글 수정 폼
    @GetMapping("{boardCode}/edit/{id}")
    public String postForm(@PathVariable Long id, Model model) {
        Post post = postService.postDetail(id);

        PostRequestDTO dto = new PostRequestDTO();
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        dto.setPinned(post.isPinned());
        model.addAttribute("post", dto);
        model.addAttribute("postId", id);
        return "community/postForm";
    }

    // 게시글 등록
    @PostMapping("/save")
    public String createPost(PostRequestDTO dto) {
        Long id = postService.postSave(dto);
        return "redirect:/community/" + id;
    }

    // 게시글 수정
    @PostMapping("/edit/{id}") // 폼 태그의 action 주소와 맞춰야 합니다.
    public String updatePost(@PathVariable Long id, PostRequestDTO dto) {
        postService.postUpdate(id, dto);
        return "redirect:/community/" + id;
    }

    // 공통 method
    private String fillCommunityModel(BoardResponseDTO board,
                                      CategoryResponseDTO category,
                                      int page, Model model) {
        Long bId = board.getId();
        Long cId = (category != null) ? category.getId() : null;

        model.addAttribute("board", board); // 이제 DTO를 바로 모델에 담음
        model.addAttribute("category", category);
        model.addAttribute("boards", boardService.findAll());
        model.addAttribute("categories", categoryService.findAllByBoardId(bId));
        model.addAttribute("posts", postService.postList(bId, cId, page));

        // HTML에서 'active' 표시를 위해 필요한 ID값들
        model.addAttribute("boardId", bId);
        model.addAttribute("categoryId", cId);

        model.addAttribute("currentPage", page); // 일단 담아두기만 하세요!
        model.addAttribute("posts", postService.postList(board.getId(),
                (category != null ? category.getId() : null), page));

        return "community/postList";
    }
}
