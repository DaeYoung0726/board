package com.Board.project_board.jwt;

import com.Board.project_board.config.auth.PrincipalDetails;
import com.Board.project_board.config.auth.PrincipalDetailsService;
import io.jsonwebtoken.Jwts;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.*;

import static com.Board.project_board.jwt.JwtProperties.*;

@Component
public class JwtUtil {

    private final SecretKey secretKey;
    private final PrincipalDetailsService principalDetailsService;

    public JwtUtil(PrincipalDetailsService principalDetailsService) {
        secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        this.principalDetailsService = principalDetailsService;
    }

    /* 토큰(claim)에서 username 가져오기 */
    public String getUsername(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
    }

    /* 토큰(claim)에서 권한(role) 가져오기 */
    public String getRole(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    /* 토큰(claim)에 저장된 category가 refresh, access인지 확인 */
    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    /* 토큰에 지정한 만료 시간 확인*/
    public boolean isExpired(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
    }

    // 사용자 정보 조회 메서드
    public PrincipalDetails getUserDetails(String username) {
        return (PrincipalDetails) principalDetailsService.loadUserByUsername(username);
    }

    // 토큰의 유효성 검사 메서드
    public Boolean validateToken(String token, PrincipalDetails principalDetails) {
        String username = getUsername(token);
        return (username.equals(principalDetails.getUsername()) && !isExpired(token));
    }

    /* access Token 발급 */
    public String generateAccessToken(PrincipalDetails principalDetails) {
        Collection<? extends GrantedAuthority> authorities = principalDetails.getAuthorities();
        Iterator<? extends  GrantedAuthority> iterator = authorities.iterator();
        String role = iterator.next().getAuthority();
        role = role.split("_")[1];      // ROLE_ 접두사 빼기 위해.

        Map<String, Object> claims = new HashMap<>();
        claims.put("category", "access");
        claims.put("role", role);

        return createJwt(claims, principalDetails.getUsername(), ACCESS_EXPIRATION_TIME);
    }

    /* refresh Token 발급 */
    public String generateRefreshToken(PrincipalDetails principalDetails) {
        Collection<? extends GrantedAuthority> authorities = principalDetails.getAuthorities();
        Iterator<? extends  GrantedAuthority> iterator = authorities.iterator();
        String role = iterator.next().getAuthority();
        role = role.split("_")[1];      // ROLE_ 접두사 빼기 위해.

        Map<String, Object> claims = new HashMap<>();
        claims.put("category", "refresh");
        claims.put("role", role);

        return createJwt(claims, principalDetails.getUsername(), REFRESH_EXPIRATION_TIME);
    }

    /* 토큰 생성 */
    private String createJwt(Map<String, Object> claims, String subject, Long expirationTime) {

        return Jwts.builder()
                .subject(subject)
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))     // JWT의 발행 시간을 설정
                .expiration(new Date(System.currentTimeMillis() + expirationTime))  // 만료 시간 설정.
                .signWith(secretKey)        //  JWT에 서명을 추가. JWT의 무결성을 보장하기 위해 사용.
                .compact();     // 설정된 정보를 기반으로 JWT를 생성하고 문자열로 직렬화.
    }
}

/*
@Component
public class JWTUtil {

    private final SecretKey secretKey;
    private final CustomUserDetailsService customUserDetailsService;

    public JWTUtil(@Value("${spring.jwt.secret}") String secret, CustomUserDetailsService customUserDetailsService) {

        secretKey = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), Jwts.SIG.HS256.key().build().getAlgorithm());
        // String은 JWT에서 사용하지 않기에 secretKey 객체를 만듦.
        this.customUserDetailsService = customUserDetailsService;
    }

    // 아래 3개는 검증을 진행할 메서드
    public String getUsername(String token) {
        // 토큰에서 subject 데이터를 추출하여 반환.
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getSubject();
    }

    // verifyWith를 통해 가지고 있는 secretKey를 가지고 토큰이 우리 서버에서 생성되었는지. 우리 서버에서 있는 키와 같은지

        // Jwts.parser(): JWT 라이브러리의 파서를 생성합니다. 이 파서는 JWT 토큰을 구문 분석하고 검증하는 데 사용됩니다.
        //                              (파싱)
        //verifyWith(secretKey): JWT 서명을 확인하기 위해 사용됩니다. secretKey는 서명을 생성할 때 사용된 비밀키입니다. 이를 통해 JWT의 무결성을 보장.
        //
        //build(): 파서를 빌드하여 파싱 및 검증을 위한 완전한 객체를 생성합니다.
        //
        //parseSignedClaims(token): 주어진 JWT 토큰을 구문 분석하고 서명된 클레임을 추출합니다. 이 메서드는 JWT 토큰의 서명을 확인하고, 만약 토큰이 유효하지 않다면 예외를 throw합니다.
        //             클레임이란, 토큰에 포함된 정보를 나타냅니다. 사용자에 대한 추가적인 데이터를 포함할 수 있으며, 토큰이 검증되고 파싱된 후에 애플리케이션에서 사용될 수 있습니다.
        //getPayload().get("username", String.class): 파싱된 클레임에서 "username" 키의 값을 추출합니다. 이 값은 해당 토큰에 포함된 사용자 이름을 나타냅니다. 반환되는 값은 String 형식입니다.

    public String getRole(String token) {       // Role 값을 가져와서 검증.

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("role", String.class);
    }

    public Boolean isExpired(String token) {    // 토큰이 만료되었는지 확인하는 메서드

        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().getExpiration().before(new Date());
                //  before(new Date()) 시간이 현재 이전인지. 이전이면 만료가 안됨.
    }

    public String getCategory(String token) {
        return Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload().get("category", String.class);
    }

    // 사용자 정보 조회 메서드
    public CustomUserDetails getUserDetails(String username) {
        return (CustomUserDetails) customUserDetailsService.loadUserByUsername(username);
    }

    // 토큰의 유효성 검사 메서드
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsername(token);
        return (username.equals(userDetails.getUsername()) && !isExpired(token));
    }

    // Access Token 생성 메서드
    public String generateAccessToken(CustomUserDetails customUserDetails) {

        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();   // Role 값 뽑아내기.
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        Map<String, Object> claims = new HashMap<>();
        claims.put("category", "access");
        claims.put("role", auth.getAuthority());

        return createJwt(claims, customUserDetails.getUsername(), ACCESS_EXPIRATION_TIME);
    }

    // Refresh Token 생성 메서드
    public String generateRefreshToken(CustomUserDetails customUserDetails) {

        Collection<? extends GrantedAuthority> authorities = customUserDetails.getAuthorities();   // Role 값 뽑아내기.
        Iterator<? extends GrantedAuthority> iterator = authorities.iterator();
        GrantedAuthority auth = iterator.next();

        Map<String, Object> claims = new HashMap<>();
        claims.put("category", "refresh");
        claims.put("role", auth.getAuthority());

        return createJwt(claims, customUserDetails.getUsername(), REFRESH_EXPIRATION_TIME);
    }

    // 토큰을 생성할 메서드
    private String createJwt(Map<String, Object> claims, String subject, Long expirationTime) {
        System.out.println("토큰 생성 실행");
        return Jwts.builder()       // JWT를 생성하기 위한 빌더 객체를 생성
                .subject(subject)   //  "sub" (Subject) 클레임을 설정하는 메소드. Subject 클레임은 토큰이 대표하는 주체를 나타냄. 주로 사용자의 식별자나 사용자 이름과 같은 정보가 이 위치에 들어감.
                .claims(claims)
                /*.claim("category", category)
                .claim("username", username)    // JWT의 페이로드에 "username"이라는 클레임을 추가
                .claim("role", role)        // JWT의 페이로드에 "role"이라는 클레임을 추가
                .issuedAt(new Date(System.currentTimeMillis()))     // JWT의 발행 시간을 설정
                        .expiration(new Date(System.currentTimeMillis() + expirationTime))   // JWT의 만료 시간을 설정
                        .signWith(secretKey)    //  JWT에 서명을 추가. JWT의 무결성을 보장하기 위해 사용. VERIFY SIGNATURE 이부분
                        .compact(); // 설정된 정보를 기반으로 JWT를 생성하고 문자열로 직렬화.
                        }
                        }
 */
