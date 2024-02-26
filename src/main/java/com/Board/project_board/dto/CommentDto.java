package com.Board.project_board.dto;

import com.Board.project_board.entity.Comment;
import com.Board.project_board.entity.Post;
import com.Board.project_board.entity.User;
import jakarta.validation.constraints.NotBlank;
import lombok.*;


public class CommentDto {

    @AllArgsConstructor
    @NoArgsConstructor
    @Setter @Getter
    @Builder
    public static class Request {

        @NotBlank
        private String content;
        private Post post;
        private User user;

        // dto -> entity
        public Comment toEntity() {
            Comment comment = Comment.builder()
                    .content(content)
                    .post(post)
                    .user(user)
                    .build();

            return comment;
        }
    }

    @AllArgsConstructor
    @NoArgsConstructor
    @Getter @Setter
    @Builder
    public static class UpdateRequest {     // 업데이트 전용 DTO

        @NotBlank
        private String content;
    }

    @Getter
    public static class Response {
        private final Long id;
        private final String content;
        private final String created_time;
        private final String modified_time;
        private final Long post_id;
        private final Long user_id;

        // entity -> dto
        public Response(Comment comment) {
            this.id = comment.getId();
            this.content = comment.getContent();
            this.created_time = comment.getCreated_time();
            this.modified_time = comment.getModified_time();
            this.user_id = comment.getUser().getId();
            this.post_id = comment.getPost().getId();
        }
    }
}
