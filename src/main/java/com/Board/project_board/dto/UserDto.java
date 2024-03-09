package com.Board.project_board.dto;


import com.Board.project_board.config.oauth.provider.OAuth2UserInfo;
import com.Board.project_board.entity.Role;
import com.Board.project_board.entity.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.io.Serializable;
import java.util.List;
import java.util.stream.Collectors;

public class UserDto {

    @Getter @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {

        @NotBlank(message = "이름은 필수 입력 값입니다.")
        private String name;
        @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-z0-9-_]{4,20}$", message = "아이디는 특수문자를 제외한 4~20자리여야 합니다.")
        private String username;
        @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
        private String nickname;
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        private String password;
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        private String email;
        private Role role;

        // dto -> entity
        public User toEntity() {
            User user = User.builder()
                    .name(name)
                    .username(username)
                    .nickname(nickname)
                    .password(password)
                    .email(email)
                    .role(Role.BRONZE)
                    .build();

            return user;
        }

        public User toEntity(OAuth2UserInfo oAuth2UserInfo, BCryptPasswordEncoder bCryptPasswordEncoder) {  // OAuth2를 위해.
            User user = User.builder()
                    .name(oAuth2UserInfo.getName())
                    .username(oAuth2UserInfo.getUsername())
                    .nickname(oAuth2UserInfo.getNickname())
                    .password(bCryptPasswordEncoder.encode(oAuth2UserInfo.getName()))
                    .email(oAuth2UserInfo.getEmail())
                    .role(Role.BRONZE)
                    .provider(oAuth2UserInfo.getProvider())
                    .build();
            return user;
        }
    }

    @Getter @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class UpdateRequest {     // 업데이트 전용 DTO

        @Pattern(regexp = "^[ㄱ-ㅎ가-힣a-zA-Z0-9-_]{2,10}$", message = "닉네임은 특수문자를 제외한 2~10자리여야 합니다.")
        private String nickname;
        @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W).{8,16}", message = "비밀번호는 8~16자 영문 대 소문자, 숫자, 특수문자를 사용하세요.")
        private String password;
        @NotBlank(message = "이메일은 필수 입력 값입니다.")
        private String email;

    }
    @Getter
    public static class Response {
        private final Long id;
        private final String name;
        private final String username;
        private final String nickname;
        private final String email;
        private final Role role;
        private final String provider;
        private final String created_time;
        private final String modified_time;
        private final int postSize;
        private final int commentSize;
        //private final List<PostDto.Response> posts;
        //private final List<CommentDto.Response> comments;

        // entity -> dto
        public Response(User user) {
            this.id = user.getId();
            this.name = user.getName();
            this.username = user.getUsername();
            this.nickname = user.getNickname();
            this.email = user.getEmail();
            this.role = user.getRole();
            this.provider = user.getProvider();
            this.created_time = user.getCreated_time();
            this.modified_time = user.getModified_time();
            this.postSize = user.getPosts().size();
            this.commentSize = user.getComments().size();
            //this.posts = user.getPosts().stream().map(PostDto.Response::new).collect(Collectors.toList());
            //this.comments = user.getComments().stream().map(CommentDto.Response::new).collect(Collectors.toList());
        }
    }
}
