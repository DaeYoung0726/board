package com.Board.project_board.controller;

import com.Board.project_board.dto.UserDto;
import com.Board.project_board.service.UserService;
import com.Board.project_board.validator.UserValidator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.Map;

@Slf4j
@Controller
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final UserValidator userValidator;

    @InitBinder
    public void init(WebDataBinder dataBinder) {
        dataBinder.addValidators(userValidator);
    }

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

    @PostMapping("/joinForm")
    public String join(@Valid UserDto.Request user, BindingResult bindingResult, Model model) {
        // 검증에 실패한 경우 joinForm으로 이동
        if (bindingResult.hasErrors()) {
            model.addAttribute("user", user);   // 회원가입 실패시, 입력 데이터를 유지

            log.info("errors = {}", bindingResult);
            /* 회원가입 실패시 message 값들을 모델에 매핑해서 View로 전달 */
            model.addAttribute("message", "조건에 맞게 입력해주세요.");
            return "joinForm";
        }

        userService.create(user);

        return "redirect:/loginForm";

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

