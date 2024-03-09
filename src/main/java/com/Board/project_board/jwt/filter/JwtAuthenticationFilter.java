package com.Board.project_board.jwt.filter;

import com.Board.project_board.config.auth.PrincipalDetails;
import com.Board.project_board.config.handler.CustomAuthFailureHandler;
import com.Board.project_board.jwt.JwtUtil;
import com.Board.project_board.dto.AuthDTO;
import com.Board.project_board.jwt.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.io.IOException;

import static com.Board.project_board.jwt.JwtProperties.*;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final CustomAuthFailureHandler customAuthFailureHandler;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {


        String username = obtainUsername(request);
        String password = obtainPassword(request);

        // 스프링 시큐리티에서 username과 password를 검증하기 위해서는 token에 담아야 함
        UsernamePasswordAuthenticationToken authToken =
                new UsernamePasswordAuthenticationToken(username, password, null);

        return authenticationManager.authenticate(authToken);
    }

    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authentication) throws IOException, ServletException {

        PrincipalDetails principalDetails = (PrincipalDetails) authentication.getPrincipal();

        String accessToken = jwtUtil.generateAccessToken(principalDetails);     // Access Token 발급
        String refreshToken = jwtUtil.generateRefreshToken(principalDetails);   // Refresh Token 발급

        String username = principalDetails.getUsername();

        if(authService.existsByUsername(username)) {
            authService.delete(username);
        }

        String refreshUUID = getRefreshUUID(refreshToken, username);

        response.addHeader(ACCESS_HEADER_VALUE, TOKEN_PREFIX + accessToken);    // 헤더에 access Token 추가
        response.addCookie(createCookie(REFRESH_COOKIE_VALUE, refreshUUID));        // 쿠키에 refresh Token Index 값 추가.
        response.setStatus(HttpServletResponse.SC_OK);


    }

    /* Refresh Token db저장 및 key값 가져오기 */
    private String getRefreshUUID(String refreshToken, String username) {
        AuthDTO authDTO = AuthDTO.builder()
                .token(refreshToken)
                .username(username)
                .build();

        return authService.save(authDTO);
    }

    /* 쿠키 생성 */
    private Cookie createCookie(String key, String value) {
        Cookie cookie = new Cookie(key, value);
        cookie.setMaxAge(24*60*60);
        //cookie.setSecure(true);
        //cookie.setPath("/");
        cookie.setHttpOnly(true);

        return cookie;
    }

    /* 로그인 실패 시 */
    @Override
    protected void unsuccessfulAuthentication(HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) throws IOException, ServletException {
        customAuthFailureHandler.onAuthenticationFailure(request, response, failed);
    }
}
