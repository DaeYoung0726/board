package com.Board.project_board.dto;

import com.Board.project_board.entity.Category;
import lombok.*;

import java.util.List;
import java.util.stream.Collectors;

public class CategoryDto {

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Request {
        private String name;

        public Category toEntity() {
            Category category = Category.builder()
                    .name(name)
                    .build();
            return category;
        }
    }

    @Getter
    public static class Response {
        private final String name;
        //private final List<PostDto.Response> posts;

        public Response(Category category) {
            this.name = category.getName();
            //this.posts = category.getPosts().stream().map(PostDto.Response::new).collect(Collectors.toList());
        }
    }
}
