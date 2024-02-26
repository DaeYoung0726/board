package com.Board.project_board.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Comment extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "content")
    private String content;

    @ManyToOne     // N:1 관계
    @JoinColumn(name = "post_id")    // 외래 키 지정
    private Post post;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;


    public void update(String content) {
        this.content = content;
    }
}
