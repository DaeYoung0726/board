package com.Board.project_board.controller;

import com.Board.project_board.config.auth.PrincipalDetails;
import com.Board.project_board.dto.CommentDto;
import com.Board.project_board.dto.UserDto;
import com.Board.project_board.service.CommentService;
import com.Board.project_board.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class CommentController_REST {

    private final CommentService commentService;
    private final UserService userService;

    /* create */
    @PostMapping("/post/{id}/comment")
    public ResponseEntity<String> save(@PathVariable Long id,
                               @AuthenticationPrincipal PrincipalDetails principalDetails, @RequestBody CommentDto.Request comment) {
        try {

            commentService.save(comment, principalDetails.getUser().getId(), id);
            log.info("Comment successfully saved by user {} on post {}", principalDetails.getUsername(), id);

            UserDto.Response dto = userService.findById(principalDetails.getUser().getId());    // 회원 자동 등업 확인.
            if(userService.checkRoleUpgrade(dto)) {
                userService.roleUpdate(dto.getId());
                log.info("User {} has been upgraded to the next role level", principalDetails.getUsername());
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
    public ResponseEntity<String> update(@PathVariable Long post_id, @PathVariable Long id, @RequestBody CommentDto.Request dto) {
        try {

            commentService.update(post_id, id, dto);
            log.info("Comment with ID {} on post {} was updated", id, post_id);
            return ResponseEntity.ok("댓글 수정 완료.");
        } catch(Exception e) {
            log.error("Failed to update comment with ID {} on post {}", id, post_id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 수정 실패.");
        }
    }

    /* delete */
    @DeleteMapping("/post/{post_id}/comment/{id}")
    public ResponseEntity<String> delete(@PathVariable Long post_id, @PathVariable Long id) {

        try {
            commentService.delete(post_id, id);
            log.info("Comment with ID {} on post {} was deleted", id, post_id);
            return ResponseEntity.ok("댓글 삭제 완료.");
        } catch(Exception e) {
            log.error("Failed to delete comment with ID {} on post {}", id, post_id, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("댓글 삭제 실패.");
        }
    }
}
