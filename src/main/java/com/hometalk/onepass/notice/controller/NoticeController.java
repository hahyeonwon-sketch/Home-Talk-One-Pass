package com.hometalk.onepass.notice.controller;

import com.hometalk.onepass.notice.dto.NoticeRequestDto;
import com.hometalk.onepass.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequiredArgsConstructor
@RequestMapping("/notice")
public class NoticeController {

    private final NoticeService noticeService;

    // 공지 목록 + 키워드 검색
    @GetMapping
    public String noticeList(@RequestParam(required = false) String keyword, Model model) {
        if (keyword == null || keyword.trim().isEmpty()) {
            model.addAttribute("notices", noticeService.getNoticeList());
        } else {
            model.addAttribute("notices", noticeService.searchNotice(keyword));
        }
        return "notice/list";
    }

    // 공지 상세 (조회수 +1)
    @GetMapping("/{id}")
    public String noticeDetail(@PathVariable Long id, Model model) {
        model.addAttribute("notice", noticeService.getNoticeDetail(id));
        model.addAttribute("attachments", noticeService.getAttachments(id));
        model.addAttribute("prevNotice", noticeService.getPreNotice(id));
        model.addAttribute("nextNotice", noticeService.getNextNotice(id));
        return "notice/detail";
    }

    // 공지 작성 페이지
    @GetMapping("/write")
    public String noticeWriteForm() {
        return "notice/write";
    }

    // 공지 작성 처리
    @PostMapping("/write")
    public String noticeWrite(@ModelAttribute NoticeRequestDto noticeRequestDto,
                              @RequestParam(required = false) MultipartFile file) {
        noticeService.createNotice(noticeRequestDto, file);
        return "redirect:/notice";
    }

    // 공지 수정 페이지 (조회수 증가 없는 단순 조회)
    @GetMapping("/{id}/edit")
    public String noticeEditForm(@PathVariable Long id, Model model) {
        model.addAttribute("notice", noticeService.getNotice(id));
        return "notice/edit";
    }

    // 공지 수정 처리
    @PostMapping("/{id}/edit")
    public String noticeEdit(@PathVariable Long id,
                             @ModelAttribute NoticeRequestDto noticeRequestDto) {
        noticeService.updateNotice(id, noticeRequestDto);
        return "redirect:/notice/" + id;
    }

    // 공지 삭제
    @PostMapping("/{id}/delete")
    public String noticeDelete(@PathVariable Long id) {
        noticeService.deleteNotice(id);
        return "redirect:/notice";
    }
}