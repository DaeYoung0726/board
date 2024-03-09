package com.Board.project_board.dto;

import com.Board.project_board.entity.Auth;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter @Setter
@Builder
public class AuthDTO {

    private String username;
    private String token;

    public Auth toEntity() {
        return Auth.builder()
                .username(username)
                .token(token)
                .revoked(false)
                .build();
    }
}
