package com.Board.project_board.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@ToString(exclude = "comments")
public class Post extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "title")
    private String title;

    @NotBlank
    @Column(name = "content")
    private String content;


    @Column(name = "view")
    private int view;

    @Column(name = "likeCount")
    private int likeCount;

    @Column(name = "report")
    private int report;

    // post가 주인. , 부모 엔터티가 삭제될 때 자식 엔터티도 함께 삭제
    @OneToMany(mappedBy = "post", cascade = CascadeType.REMOVE)
    @OrderBy("id asc") // 댓글 정렬
    //@JsonIgnoreProperties("post")
    private List<Comment> comments;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    public void update(String title, String content) {
        this.title = title;
        this.content = content;
    }


}





