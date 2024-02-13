package com.Board.project_board.controller;

import com.Board.project_board.config.auth.PrincipalDetails;
import com.Board.project_board.dto.PostDto;
import com.Board.project_board.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@Controller
@RequestMapping("/post")
public class PostController {

    private final PostService postService;

    @GetMapping("/write")
    public String write() {
        return "board_write";
    }

    @GetMapping("/list")
    public String getAll(Model model) {
        model.addAttribute("list", postService.findAll());
        return "board_list";
    }

    @GetMapping("/read/{id}")
    public @ResponseBody String read(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetails principalDetails) {
        PostDto.Response post = postService.findById(id);
        if(post.getUser_id() != principalDetails.getUser().getId()) {
            postService.updateView(id);
            return "글 불러오기 && 조회수 업데이트 완료.";
        }
        return "글 불러오기.";
    }
}
