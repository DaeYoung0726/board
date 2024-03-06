package com.Board.project_board.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.List;

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

    @NotBlank
    @Column(name = "name")
    private String name;

    @NotBlank
    @Column(name = "username", unique = true)
    private String username;

    @NotBlank
    @Column(name = "nickname", unique = true)
    private String nickname;

    @Column(name = "password")
    private String password;

    @NotBlank
    @Column(name = "email", unique = true)
    private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    /** 아래는 OAuth를 위한 필드.*/
    private String provider;


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
