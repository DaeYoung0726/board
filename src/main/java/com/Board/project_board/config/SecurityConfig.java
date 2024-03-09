package com.Board.project_board.config;

import com.Board.project_board.config.auth.PrincipalDetailsService;
import com.Board.project_board.config.handler.CustomAuthFailureHandler;
import com.Board.project_board.config.handler.CustomLogoutHandler;
import com.Board.project_board.config.oauth.PrincipalOauth2UserService;
import com.Board.project_board.jwt.JwtUtil;
import com.Board.project_board.jwt.filter.JwtAuthenticationFilter;
import com.Board.project_board.jwt.filter.JwtAuthorizationFilter;
import com.Board.project_board.jwt.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final PrincipalOauth2UserService principalOauth2UserService;    // OAuth2 로그인을 위해.
    private final PrincipalDetailsService principalDetailsService;          // 로그인 기억을 위해.
    private final CustomAuthFailureHandler customAuthFailureHandler;        // 로그인 에러를 위해.
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JwtUtil jwtUtil;
    private final AuthService authService;
    private final CustomLogoutHandler customLogoutHandler;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)

                .formLogin(AbstractHttpConfigurer::disable)     // jwt 토큰을 받을 것이기에. fromLogin, httpBasic 비활성화.

                .httpBasic(AbstractHttpConfigurer::disable)

                .authorizeHttpRequests(authorize ->         //  HTTP 요청에 대한 인가 규칙을 설정
                        authorize                   // 아래와 같이 전체적으로 권한을 설정.
                                // 위에 @EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)이거는 특정 사이트만.
                                // 아래 인가 먼저 등록한 것부터 동작한다.
                                .requestMatchers(new AntPathRequestMatcher("/user/**")).authenticated() // 인증된(authenticated) 사용자에게만 허용
                                .requestMatchers(new AntPathRequestMatcher("/user_silver/**")).hasAnyRole("ADMIN", "MANAGER", "SILVER", "GOLD")
                                .requestMatchers(new AntPathRequestMatcher("/user_gold/**")).hasAnyRole("ADMIN", "MANAGER", "GOLD")
                                .requestMatchers(new AntPathRequestMatcher("/manager/**")).hasAnyRole("ADMIN", "MANAGER") //"ADMIN" 또는 "MANAGER" 역할을 가진 사용자에게만 허용
                                .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasAnyRole("ADMIN") // "ADMIN" 역할을 가진 사용자에게만 허용
                                .anyRequest().permitAll())   // 나머지 요청에 대해서는 모든 권한을 허용

                .sessionManagement((session) ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                .oauth2Login(oauth2 ->
                        oauth2
                                .userInfoEndpoint(userInfoEndpointConfig ->
                                        userInfoEndpointConfig.userService(principalOauth2UserService))
                                .successHandler())

                .addFilterAt(new JwtAuthenticationFilter(
                        authenticationManager(authenticationConfiguration), jwtUtil, authService, customAuthFailureHandler),
                        UsernamePasswordAuthenticationFilter.class)

                .addFilterBefore(new JwtAuthorizationFilter(jwtUtil), UsernamePasswordAuthenticationFilter.class)

                .logout(logout ->
                        logout.addLogoutHandler(customLogoutHandler));

        return http.build();
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {

        return configuration.getAuthenticationManager();
    }

    /*@Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)       // CSRF 공격 방지를 위한 기능을 비활성화. csrf 토큰도 같이 보내야 로그인 되기에 비활성화 함.
                // https://www.youtube.com/watch?v=l8xjecnAzMw&list=PLJkjrxxiBSFCKD9TRKDYn7IE96K2u3C3U&index=13 여기서 csrf 사용하면서 쓰는 법 있음.
                .sessionManagement(sessionManagement ->     // https://cornarong.tistory.com/81
                        sessionManagement
                                .sessionFixation().newSession()
// 세션 고정 공격 방지 none() : 로그인 시 세션 정보 변경 안함. newSession() : 로그인 시 세션 새로 생성. changeSessionId() : 로그인 시 동일한 세션에 대한 id 변경
                                .sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)   //  세션이 필요하면 생성하도록 셋팅
                                .maximumSessions(1)         // 최대 세션 수를 1로 설정
                                .maxSessionsPreventsLogin(true)     // 최대 세션 수에 도달하면 로그인을 막음
                                .expiredUrl("/loginForm"))          // 세션이 만료된 경우 로그인 페이지로 이동
                .authorizeHttpRequests(authorize ->         //  HTTP 요청에 대한 인가 규칙을 설정
                        authorize                   // 아래와 같이 전체적으로 권한을 설정.
                                // 위에 @EnableMethodSecurity(securedEnabled = true, prePostEnabled = true)이거는 특정 사이트만.
                                // 아래 인가 먼저 등록한 것부터 동작한다.
                                .requestMatchers(new AntPathRequestMatcher("/user/**")).authenticated() // 인증된(authenticated) 사용자에게만 허용
                                .requestMatchers(new AntPathRequestMatcher("/user_silver/**")).hasAnyRole("ADMIN", "MANAGER", "SILVER", "GOLD")
                                .requestMatchers(new AntPathRequestMatcher("/user_gold/**")).hasAnyRole("ADMIN", "MANAGER", "GOLD")
                                .requestMatchers(new AntPathRequestMatcher("/manager/**")).hasAnyRole("ADMIN", "MANAGER") //"ADMIN" 또는 "MANAGER" 역할을 가진 사용자에게만 허용
                                .requestMatchers(new AntPathRequestMatcher("/admin/**")).hasAnyRole("ADMIN") // "ADMIN" 역할을 가진 사용자에게만 허용
                                .anyRequest().permitAll())   // 나머지 요청에 대해서는 모든 권한을 허용
                .formLogin(formLogin ->            // Spring Security 구성을 정의하는 데 사용되는 메서드
                        formLogin
                                .loginPage("/loginForm")      //  사용자를 로그인 페이지로 리다이렉션하는 데 사용
                                // /admin, /user, /manager 하면 /login으로 이동
                                .loginProcessingUrl("/login") // /login 주소가 호출되면 시큐리티가 낚아채서 대신 !!로그인 진행.
                                .failureHandler(customAuthFailureHandler)     // 로그인 에러 확인을 위한 class.
                                .defaultSuccessUrl("/"))   // 로그인하면 이 공간으로 감. (여기서는 "/"으로)
                .rememberMe(rememberMe ->
                        rememberMe
                                .key("my-secret-key") // Remember-Me 토큰을 생성할 때 사용되는 비밀 키를 설정하는 역할.
                                // "my-secret-key"는 예측하기 어려운 랜덤한 문자열로 구성된 키이며, "key"는 예시로 사용된 단순한 문자열
                                .rememberMeParameter("rememberMe")
                                .tokenValiditySeconds(30 * 24 * 60)     // 한달 동안 유지.
                                .userDetailsService(principalDetailsService))
                // 만약, /user으로 통해 login으로 가게 되면 로그인 할 시 user으로 가게 됨.
                // 즉, 원래 있던 페이지로 돌아갈 수 있음.q
                // user로 로그인하면 manager이나 admin으로 못감.
                .logout(logout ->       // 안해도 logout되긴 함. 추가적인 정보를 위해 넣은 것.
                        logout
                                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                                .logoutSuccessUrl("/loginForm")
                                .invalidateHttpSession(true) // 세션을 무효화
                                .deleteCookies("JSESSIONID")) // 세션 쿠키를 삭제

                .oauth2Login(oauth2 ->      //  OAuth 2.0 로그인을 구성하는 메서드.  구글 로그인이 완료된 뒤의 후처리가 필요함.
                        oauth2
                                .loginPage("/loginForm")
                                // Tip. oauth-client를 쓰면 코드가 아님. 엑세스토큰+사용자프로필정보 받음.)
                                .userInfoEndpoint(userInfoEndpoint ->   //  OAuth 2.0에서 사용자 정보 엔드포인트를 구성하는 메서드
                                        userInfoEndpoint
                                                .userService(principalOauth2UserService)));  // 사용자 정보를 가져오는 서비스를 설정.
                                        // principalOauth2UserService 여기에 후처리 등록.

        *//** OAuth 2.0를 사용하여 로그인할 때 사용자 정보 엔드포인트를 구성하는 이유는 사용자의 정보를 얻어오기 위해서.
         OAuth 2.0 프로토콜은 로그인 후에도 사용자에 대한 추가 정보가 필요한 경우가 많기 때문에 이 정보를 얻어오기 위해 사용자 정보 엔드포인트를 사용. *//*

        return http.build();
    }*/
}

