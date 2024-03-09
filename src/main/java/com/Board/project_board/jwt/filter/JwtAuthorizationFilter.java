package com.Board.project_board.jwt.filter;

import com.Board.project_board.config.auth.PrincipalDetails;
import com.Board.project_board.entity.Role;
import com.Board.project_board.entity.User;
import com.Board.project_board.jwt.JwtUtil;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.Board.project_board.jwt.JwtProperties.ACCESS_HEADER_VALUE;
import static com.Board.project_board.jwt.JwtProperties.TOKEN_PREFIX;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthorizationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String access = request.getHeader(ACCESS_HEADER_VALUE);

        if(access == null || !access.startsWith(TOKEN_PREFIX)) {
            log.info("token is null");

            filterChain.doFilter(request, response);

            return;     // 조건이 해당되면 메서드 종료.
        }

        String accessToken = access.substring(TOKEN_PREFIX.length()).trim();     // 순수 토큰만 얻기.

        try {
            jwtUtil.isExpired(accessToken);     // 만료 되었는지.
        } catch (ExpiredJwtException e) {
            handleExpiredToken(response);
            return;
        }

        String accessCategory = jwtUtil.getCategory(accessToken);

        if(!"access".equals(accessCategory)) {         // jwt에 담긴 category를 통해 access 가 맞는지 확인.
            handleInvalidToken(response);
            return;
        }

        String username = jwtUtil.getUsername(accessToken);
        String role = jwtUtil.getRole(accessToken);

        User userEntity = new User();
        userEntity.setUsername(username);
        userEntity.setRole(Role.valueOf(role));

        PrincipalDetails principalDetails = new PrincipalDetails(userEntity);

        Authentication authToken = new UsernamePasswordAuthenticationToken(principalDetails, null, principalDetails.getAuthorities());

        SecurityContextHolder.getContext().setAuthentication(authToken);

        filterChain.doFilter(request, response);
    }


    // 토큰이 access인지 확인 (발급시 페이로드에 명시)
    private void handleExpiredToken(HttpServletResponse response) throws IOException {
        log.info("Access token expired");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Access token expired");
    }

    private void handleInvalidToken(HttpServletResponse response) throws IOException {
        log.info("Invalid access token");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.getWriter().write("Invalid access token");
    }

}
