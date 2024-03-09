package com.Board.project_board.jwt;

public interface JwtProperties {
    Long ACCESS_EXPIRATION_TIME = 20*1000L;
    Long REFRESH_EXPIRATION_TIME = 60*60*1000L;
    String SECRET_KEY = "vmfhaltmskdlstkfkdgodyroqkfwkdbalroqkfwkdbalaaaaaaaaaaaaaaaabbbbb";    // 임의로 만든 암호화 변수 키
    String TOKEN_PREFIX = "Bearer ";
    String ACCESS_HEADER_VALUE = "Authorization";
    String REFRESH_COOKIE_VALUE = "refresh";
}

