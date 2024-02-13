package com.Board.project_board.dto;

import com.Board.project_board.entity.Category;
import com.Board.project_board.entity.Post;

import com.Board.project_board.entity.User;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class PostDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    @Builder
    public static class Request {
        private String title;
        private String content;
        private User user;
        private Category category;
        private int view;
        private int likeCount;
        private int report;

        // dto -> entity
        public Post toEntity() {
            Post post = Post.builder()
                    .title(title)
                    .content(content)
                    .user(user)
                    .category(category)
                    .view(view)
                    .likeCount(likeCount)
                    .report(report)
                    .build();

            return post;
        }
    }
    @Getter
    public static class Response {
        private final Long id;
        private final String title;
        private final String content;
        private final int view;
        private final int likeCount;
        private final int report;
        private final String created_time;
        private final String modified_time;
        private final Long user_id;
        private final Long category_id;
        //private final List<CommentDto.Response> comments;

        // entity -> dto
        public Response(Post post) {
            this.id = post.getId();
            this.title = post.getTitle();
            this.content = post.getContent();
            this.view = post.getView();
            this.likeCount = post.getLikeCount();
            this.report = post.getReport();
            this.created_time = post.getCreated_time();
            this.modified_time = post.getModified_time();
            this.user_id = post.getUser().getId();
            this.category_id = post.getCategory().getId();
            //this.comments = post.getComments().stream().map(CommentDto.Response::new).collect(Collectors.toList());
        }
    }

}

