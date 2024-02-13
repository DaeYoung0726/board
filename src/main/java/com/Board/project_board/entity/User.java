package com.Board.project_board.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder    // 생성자가 아닌 class에 붙여준 이유는 @AllArgsConstructor을 했기 때문.
@Entity
@ToString(exclude = {"posts", "comments"})
public class User extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @Column(name = "name")
    private String name;

    @NotNull
    @Column(name = "userid", unique = true)
    private String userId;

    @NotNull
    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column(name = "password")
    private String password;

    @NotNull
    @Column(name = "email", unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @NotNull
    @Column(name = "role")
    private Role role;

    /** 아래 두 개는 OAuth를 위한 필드.*/
    private String provider;
    private String providerId;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    //@JsonIgnoreProperties("user")
    private List<Post> posts;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE)
    //@JsonIgnoreProperties("user")
    private List<Comment> comments;

    // 이미 있을 경우 최근 로그인 시간만 업데이트.
    public User updateModifiedDateIfUserExists() {
        this.onPreUpdate();
        return this;
    }

    public void update(String nickname, String password, String email) {
        this.nickname = nickname;
        this.password = password;
        this.email = email;
    }

    public void roleUpdate(Role role) {
        this.role = role;
    }

}
