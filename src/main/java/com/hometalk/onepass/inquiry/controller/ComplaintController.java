package com.hometalk.onepass.inquiry.controller;

import com.hometalk.onepass.inquiry.entity.Complaint;
import com.hometalk.onepass.inquiry.service.ComplaintService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/complaint")
public class ComplaintController {

    private final ComplaintService complaintService;

    // 민원 등록
    @PostMapping
    public Long register(@RequestBody Complaint complaint) {
        return complaintService.register(complaint);
    }

    // 전체 민원 조회
    @GetMapping
    public List<Complaint> findAll() {
        return complaintService.findAll();
    }

    // 상세 민원 조회
    @GetMapping("/{id}")
    public Complaint findOne(@PathVariable Long id) {
        return complaintService.findOne(id);
    }

    // 관리자 답변 등록
    @PostMapping("/{id}")
    public void respond(@PathVariable Long id, @RequestBody String response) {
        complaintService.respond(id, response);
    }

    // 민원 삭제
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        complaintService.delete(id);
    }
}
