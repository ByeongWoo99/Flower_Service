package com.ttasum.memorial.controller.admin;

import com.ttasum.memorial.dto.common.ApiResponse;
import com.ttasum.memorial.dto.donationStory.response.PageResponse;
import com.ttasum.memorial.dto.UserDto;
import com.ttasum.memorial.dto.blameText.BlameTextLetterDto;
import com.ttasum.memorial.service.admin.AdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import static com.ttasum.memorial.domain.type.ContentType.COMMENT;
import static com.ttasum.memorial.domain.type.ContentType.STORY;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    // 1. HTML 회원가입 페이지 반환
    @GetMapping("/signup")
    public String signUp(Model model) {
        model.addAttribute("departments", adminService.findAllDepartment());
        model.addAttribute("positions", adminService.findAllPosition());
        model.addAttribute("authorities", adminService.findAllAuthority());

        return "admin/admin_signUp";  // html 뷰
    }

    // 2. ID 중복 검사 API
    @GetMapping("/checkId")
    @ResponseBody
    public ResponseEntity<?> checkId(@RequestParam("id") String id) {
        return adminService.duplicationId(id);  // JSON 반환
    }

    // 회원 가입 post
    @PostMapping("/signup")
    @ResponseBody
    public ResponseEntity<?> signup(@RequestBody UserDto admin) {
        return adminService.signupAdmin(admin);
    }

    // 로그인 폼
    @GetMapping("/login")
    public String getAdmin() {
        return "/admin/admin_login";
    }

    // 로그인 시도
//    @PostMapping("/login")
//    public ResponseEntity<?> login(@RequestBody AdminRequestDto request) {
//        // DB에 저장된 로그인 정보와 비교
//        return adminService.checkIdAndPw(request);
//    }

    @GetMapping("/dashboard")
    public String getDashboard(Model model) {
        return "admin/dashboard";
    }

    @GetMapping("/blameTextList")
    public String getBlameText(Model model) {
        return "/admin/blameText_dashboard";
    }

    @GetMapping("/noAuthorization")
    public String getNoAuthorization(Model model) {
        return "/admin/noAuthorization";
    }

    @GetMapping("/blameText/story")
    public String getStory(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "filter", required = false) String filter,
            @RequestParam(name = "orderBy", required = false) String orderBy,
            Model model) {
        PageRequest pageRequest = adminService.setOrderByOptions(page, size, orderBy);
        model.addAttribute("pageResponse",adminService.getBlameTextLetters(filter, orderBy, pageRequest));
        return "/admin/blameText_story";
    }

    @GetMapping("/blameText/story/detail")
    public String getStory(
            @RequestParam(name = "seq") int seq,
            Model model) {
        model.addAttribute("blameDetail", adminService.getBlameTextLettersSentences(seq));
        return "/admin/blameText_story_detail";
    }

    @DeleteMapping("/blameText/story/{originSeq}/{boardType}")
    @ResponseBody
    public ResponseEntity<?> deleteStory(
            @PathVariable int originSeq,
            @PathVariable String boardType) {
        // 삭제
        return adminService.deleteBlameText(originSeq, boardType, STORY);
    }

    @GetMapping("/blameText/comment")
    public String getComment(
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "10") int size,
            @RequestParam(name = "filter", required = false) String filter,
            @RequestParam(name = "orderBy", required = false) String orderBy,
            Model model) {
        PageRequest pageRequest = adminService.setOrderByOptions(page, size, filter);
        model.addAttribute("pageResponse", adminService.getBlameTextComment(filter, orderBy, pageRequest));
        return "/admin/blameText_comment";
    }

    @GetMapping("/blameText/comment/detail")
    public String getComment(
            @RequestParam(name = "seq") int seq,
            Model model){
        model.addAttribute("blameDetail", adminService.getBlameTextCommentSentence(seq));
        return "/admin/blameText_comment_detail";
    }

    @DeleteMapping("/blameText/comment/{originSeq}/{boardType}")
    @ResponseBody
    public ResponseEntity<?> deleteComment(
            @PathVariable(name = "originSeq") int originSeq,
            @PathVariable String boardType){
        return adminService.deleteBlameText(originSeq, boardType,COMMENT);
    }
}
