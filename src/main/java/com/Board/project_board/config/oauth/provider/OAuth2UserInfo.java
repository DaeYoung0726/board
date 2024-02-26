package com.Board.project_board.config.oauth.provider;

import com.Board.project_board.entity.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public interface OAuth2UserInfo {

    String getProvider();
    String getUserId();
    String getName();
    String getNickname();
    String getEmail();

}
