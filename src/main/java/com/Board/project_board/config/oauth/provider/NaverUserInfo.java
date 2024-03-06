package com.Board.project_board.config.oauth.provider;

import lombok.RequiredArgsConstructor;

import java.util.Map;

@RequiredArgsConstructor
public class NaverUserInfo implements OAuth2UserInfo{

    private final Map<String, Object> attributes;
    @Override
    public String getProvider() {
        return "naver";
    }

    @Override
    public String getUsername() {
        return (String) attributes.get("email");
    }

    @Override
    public String getName() {
        return (String) attributes.get("name");
    }

    @Override
    public String getNickname() {
        return attributes.get("name") + "3";
    }

    @Override
    public String getEmail() {
        return (String) attributes.get("email");
    }
}
