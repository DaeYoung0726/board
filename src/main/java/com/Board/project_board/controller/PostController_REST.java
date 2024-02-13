package com.Board.project_board.controller;

import com.Board.project_board.config.auth.PrincipalDetails;
import com.Board.project_board.dto.UserDto;
import com.Board.project_board.entity.Post;
import com.Board.project_board.service.PostService;
import com.Board.project_board.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.Board.project_board.dto.PostDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api")
public class PostController_REST {

    private final PostService postService;
    private final UserService userService;

    // create
    @PostMapping("/post/{category_name}/write")
    public ResponseEntity<String> save(@RequestBody PostDto.Request post,
                                       @PathVariable String category_name,
                                       @AuthenticationPrincipal PrincipalDetails principalDetails) {
        // @AuthenticationPrincipal을 사용해서 현재 인증된 로그인 정보를 객체로 만들어줌
        try {
            postService.save(post, category_name, principalDetails.getUser().getId());

            UserDto.Response dto = userService.findById(principalDetails.getUser().getId());    // 회원 자동 등업 확인.
            if(userService.checkRoleUpgrade(dto)) {
                userService.roleUpdate(dto.getId());
                return ResponseEntity.ok("게시글 작성 + 회원 등업 완료.");
            }
            return ResponseEntity.ok("게시글 작성 완료.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 작성 실패.");
        }
    }

    // read All (user) 이름만.
    @GetMapping("/{userId}/post")
    public Page<PostDto.Response> user_postFindAll(@PathVariable Long userId, Pageable pageable) {

        return postService.user_postFindAll(userId, pageable);
    }

    // read post
    @GetMapping("/post/{postId}")
    public PostDto.Response findById(@PathVariable Long postId) {
        return postService.findById(postId);
    }

    /* // read All       페이징 처리 x.
    @GetMapping("/post")
    public List<PostDTO.Response> findAll() {
        return postService.getAll();
    }*/

    /* 페이징 처리 read All (단어 검색 포함) */
    @GetMapping("/post")
    public Page<PostDto.Response> findAll(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                          @RequestParam(required = false) String searchKeyword) {

        if(searchKeyword == null)
            return postService.postList(pageable);
        else
            return postService.postSearchList(searchKeyword, pageable);
    }

    /* 카테고리 게시글 불러오기.(단어 검색 포함) 페이징 처리 */
    @GetMapping("/post/category/{category_name}")
    public Page<PostDto.Response> findByCategoryName(@PageableDefault(sort = "id", direction = Sort.Direction.DESC) Pageable pageable,
                                                     @RequestParam(required = false) String searchKeyword,
                                                     @PathVariable String category_name) {

        if(searchKeyword == null)
            return postService.findByCategoryName(category_name, pageable);
        else
            return postService.findByCategoryNameAndTitleContaining(category_name, searchKeyword, pageable);
    }

    // update
    @PutMapping("/post/{postId}")
    public ResponseEntity<String> modify(@PathVariable Long postId, @RequestBody PostDto.Request post) {
        try {
            postService.update(postId, post);
            return ResponseEntity.ok("게시글 수정 완료.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 수정 실패.");
        }
    }

    // delete
    @DeleteMapping("/post/{postId}")
    public ResponseEntity<String> delete(@PathVariable Long postId) {
        try {
            postService.delete(postId);
            return ResponseEntity.ok("게시글 삭제 완료.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("게시글 삭제 실패.");
        }
    }

    @PatchMapping("/post/{postId}/update-like")
    public ResponseEntity<String> updateLike(@PathVariable Long postId, @RequestParam boolean increase) {
        try {
            int value = increase ? 1 : -1;
            postService.updateLikeCount(postId, value);
            String action = increase ? "증가" : "감소";
            return ResponseEntity.ok("가게 좋아요 " + action + "완료.");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("좋아요 업데이트 실패");
        }
    }

    @PatchMapping("/post/{postId}/update-report")
    public ResponseEntity<String> updateReport(@PathVariable Long postId) {
        try {
            int result = postService.updateReport(postId);
            if(result == 1) {
                return ResponseEntity.ok("신고 5개 먹어 해당 게시글이 삭제되었습니다.");
            }
            return ResponseEntity.ok("신고 성공.");
        }
        catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("신고 실패");
        }
    }
}
