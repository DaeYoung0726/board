package com.Board.project_board.controller;

import com.Board.project_board.config.auth.PrincipalDetails;
import com.Board.project_board.dto.UserDto;
import com.Board.project_board.repository.UserRepository;
import com.Board.project_board.service.UserService;
import com.Board.project_board.service.mail.AuthCodeService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
@Slf4j
public class UserController_REST {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserService userService;
    private final AuthCodeService authCodeService;

    /* 회원 업데이트 */
    @PutMapping("/user/update")
    public ResponseEntity<String> modify(@AuthenticationPrincipal PrincipalDetails principalDetails,
                                         @Validated @RequestBody UserDto.UpdateRequest user, HttpServletResponse response) {
        try {
            userService.update(principalDetails.getUser().getId(), user);
            response.sendRedirect("/logout");           // 이 방식도 있겠지만, html도 있을듯.
            return ResponseEntity.ok("회원 수정 완료.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("회원 수정 실패.");
        }
    }

    /* 인증메일 보내기 */
    @PostMapping("/emails/verification-request")
    public ResponseEntity<String> sendMessage(@RequestParam("email") @Valid @Email String email) {

        try {
            authCodeService.sendCodeToMail(email);
            return ResponseEntity.ok("인증메일 보내기 성공.");
        } catch (Exception e) {
            log.error("Failed to send verification email: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("인증메일 보내기 실패.");
        }
    }

    /* 인증메일 확인 */
    @GetMapping("/emails/verification")
    public String verifyCode(@RequestParam("email") @Valid @Email String email,
                                             @RequestParam("code") String code) {

        if(authCodeService.verifiedCode(email, code))
            return "인증 성공";
        else
            return "인증 실패";
    }
}
