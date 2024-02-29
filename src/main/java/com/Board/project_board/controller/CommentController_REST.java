package com.Board.project_board.controller;

import com.Board.project_board.config.auth.PrincipalDetails;
import com.Board.project_board.dto.CommentDto;
import com.Board.project_board.dto.UserDto;
import com.Board.project_board.service.mail.MailService;
import com.Board.project_board.service.CommentService;
import com.Board.project_board.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CommentController_REST {

    private final CommentService commentService;
    private final UserService userService;
    private final MailService mailService;

    /* create */
    @PostMapping("/post/{id}/comment")
    public ResponseEntity<String> save(@PathVariable Long id, @AuthenticationPrincipal PrincipalDetails principalDetails,
                                       @Validated @RequestBody CommentDto.Request comment) {

        try {
            commentService.save(comment, principalDetails.getUsername(), id);

            UserDto.Response dto = userService.findByUsername(principalDetails.getUsername());    // 회원 자동 등업 확인.
            if(userService.checkRoleUpgrade(dto)) {
                userService.roleUpdate(dto.getId());
                mailService.selectMail("update", dto.getEmail(),
                        String.valueOf(dto.getRole().getNext()));
                return ResponseEntity.ok("댓글 작성 + 회원 등업 완료.");
            }
            return ResponseEntity.ok("댓글 작성 완료.");
        } catch(Exception e) {
            log.error("Failed to save comment on post {}", id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 작성 실패.");
        }
    }

    /* read */
    @GetMapping("/post/{id}/comment")
    public List<CommentDto.Response> read(@PathVariable Long id) {
        return commentService.findAll(id);
    }

    /* update */
    @PutMapping("/post/{post_id}/comment/{id}")
    public ResponseEntity<String> update(@PathVariable Long post_id, @PathVariable Long id,
                                         @Validated @RequestBody CommentDto.UpdateRequest dto,
                                         @AuthenticationPrincipal PrincipalDetails principalDetails) {

        try {
            commentService.update(post_id, principalDetails.getUsername(), id, dto);
            return ResponseEntity.ok("댓글 수정 완료.");
        } catch(Exception e) {
            log.error("Error occurred while updating comment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 수정 실패.");
        }
    }

    /* delete */
    @DeleteMapping("/post/{post_id}/comment/{id}")
    public ResponseEntity<String> delete(@PathVariable Long post_id, @PathVariable Long id,
                                         @AuthenticationPrincipal PrincipalDetails principalDetails) {

        try {
            commentService.delete(post_id, principalDetails.getUsername(), id);
            return ResponseEntity.ok("댓글 삭제 완료.");
        } catch(Exception e) {
            log.error("Error occurred while deleting comment: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제 실패.");
        }
    }
}
