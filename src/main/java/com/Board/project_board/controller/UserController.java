package com.Board.project_board.controller;

import com.Board.project_board.dto.UserDto;
import com.Board.project_board.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/user")
    public @ResponseBody String user() {
        return "user";
    }

    @GetMapping("/manager")
    public @ResponseBody String manager() {
        return "manager";
    }

    @GetMapping("/admin")
    public @ResponseBody String admin() {
        return "admin";
    }

    @GetMapping("/user_silver")
    public @ResponseBody String silver() {
        return "silver";
    }

    @GetMapping("/user_gold")
    public @ResponseBody String gold() {
        return "gold";
    }

    @GetMapping("/loginForm")
    public String loginForm(@RequestParam(value = "exception", required = false) String exception, Model model) {
        model.addAttribute("exception", exception);
        return "loginForm";
    }

    @GetMapping("/joinForm")
    public String joinForm(@ModelAttribute("user") UserDto.Request user) {  // joinForm에 있는 user과 매핑시켜주기 위해.
        // 이 부분에서는 추가할게 없어 바로 return.
        return "joinForm";
    }

    @PostMapping("/join")
    public String join(@Valid UserDto.Request user, Errors errors, Model model) {
        // 검증에 실패한 경우 joinForm으로 이동
        if (errors.hasErrors()) {
            model.addAttribute("user", user);   // 회원가입 실패시, 입력 데이터를 유지

            /* 회원가입 실패시 message 값들을 모델에 매핑해서 View로 전달 */
            Map<String, String> validateResult = userService.validateHandler(errors);
            // map.keySet() -> 모든 key값을 갖고온다.
            // 그 갖고온 키로 반복문을 통해 키와 에러 메세지로 매핑
            for (String key : validateResult.keySet()) {
                // ex) model.addAtrribute("valid_id", "아이디는 필수 입력사항 입니다.")
                model.addAttribute(key, validateResult.get(key));
            }
            model.addAttribute("message", "조건에 맞게 입력해주세요.");
            return "joinForm";
            /**
             * StringBuilder sb = new StringBuilder();      // 초기 식.
             * for(FieldError error : errors.getFieldErrors())
             *      sb.append(error.getDefaultMessage() + "\n");
             *  model.addAttribute("message",sb);
             *  model.addAttribute("searchUrl", "/joinForm");
             */
        }

        boolean exists = userService.existsByUserId(user.getUserId())
                || userService.existsByNickname(user.getNickname()) || userService.existsByEmail(user.getEmail());

        if(exists) {
            model.addAttribute("user", user);

            StringBuilder sb = new StringBuilder();

            if(userService.existsByUserId(user.getUserId()))
                sb.append("아이디 중복." + "\n");
            if(userService.existsByNickname(user.getNickname()))
                sb.append("닉네임 중복." + "\n");
            if(userService.existsByEmail(user.getEmail()))
                sb.append("이메일 중복." + "\n");

            model.addAttribute("message", sb);    // 세세히 나오도록 바꾸기
            return "/joinForm";     // 중복 오류 뜬 것은 이전 입력값이 채워지지 않도록 만들어보기
        } else {
            userService.create(user);

            return "redirect:/loginForm";
        }
    }

    @GetMapping("/session-Info")            // 세션 확인용
    public @ResponseBody String sessionInfo(HttpServletRequest req) {
        HttpSession session = req.getSession(false);
        if (session == null) {
            return "세션이 없습니다";
        }
        // session.setMaxInactiveInterval(3605);

        log.info("sessionId = {}", session.getId());
        log.info("getMaxInactiveInterval={}", session.getMaxInactiveInterval());
        // *참고 :application.yml에서 설정 가능한 최소 시간은 1분이며, 분단위로 설정해야 합니다.
        log.info("creationTime={}", new Date(session.getCreationTime()));
        log.info("lastAccessTime={}", new Date(session.getLastAccessedTime()));

        return "세션출력";

    }
}

