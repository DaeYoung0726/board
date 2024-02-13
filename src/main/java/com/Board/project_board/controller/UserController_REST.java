package com.Board.project_board.controller;

import com.Board.project_board.config.auth.PrincipalDetails;
import com.Board.project_board.dto.UserDto;
import com.Board.project_board.repository.UserRepository;
import com.Board.project_board.service.UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class UserController_REST {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserService userService;

    /**
     * 확인용
     */
    @GetMapping("/user")
    public String user(@AuthenticationPrincipal PrincipalDetails principalDetails) {
        System.out.println(principalDetails.getUser().getId());
        return "user";
    }

    @PutMapping("/user/update")
    public ResponseEntity<String> modify(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                         @RequestBody UserDto.Request user, HttpServletResponse response) {
        try {
            userService.update(principalDetails.getUser().getId(), user);
            response.sendRedirect("/logout");           // 이 방식도 있겠지만, html도 있을듯.
            return ResponseEntity.ok("회원 수정 완료.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("회원 수정 실패.");
        }
    }
}
