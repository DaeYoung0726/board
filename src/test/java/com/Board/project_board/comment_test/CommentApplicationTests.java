package com.Board.project_board.comment_test;

import com.Board.project_board.dto.CommentDto;
import com.Board.project_board.entity.Comment;
import com.Board.project_board.entity.Post;
import com.Board.project_board.entity.Role;
import com.Board.project_board.entity.User;
import com.Board.project_board.repository.PostRepository;
import com.Board.project_board.repository.UserRepository;
import com.Board.project_board.service.CommentService;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/*
@SpringBootTest
public class CommentApplicationTests {

    @Autowired
    private CommentService commentService;
    @Autowired
    private PostRepository postRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    private void checkSame(CommentDto.Response comment1, CommentDto.Response comment2) {
        Assertions.assertEquals(comment1.getContent(), comment2.getContent());
    }

    @Test
    @DisplayName("데이터가 제대로 들어갔는지 테스트")
    public void addAndgetTest() {
        String password = bCryptPasswordEncoder.encode("1234");

        User user = User.builder()
                .name("박대영")
                .userId("abcd")
                .nickname("대영")
                .password(password)
                .email("abcd@qwe.zxc")
                .role(Role.USER)
                .build();
        userRepository.save(user);
        Long user_id = user.getId();

        Post post = Post.builder()
                .title("제목1")
                .content("내용1")
                .build();
        post.setUser(user);
        postRepository.save(post);
        Long post_id = post.getId();

        Comment comment1 = Comment.builder()
                .content("댓글1")
                .build();
        Comment comment2 = Comment.builder()
                .content("댓글2")
                .build();

        CommentDto.Request comment_dto1 = CommentDto.Request.builder()
                .content(comment1.getContent())
                .build();
        CommentDto.Request comment_dto2 = CommentDto.Request.builder()
                .content(comment2.getContent())
                .build();

        Long comment_id1 = commentService.save(comment_dto1, user_id, post_id);
        Long comment_id2 = commentService.save(comment_dto2, user_id, post_id);


        CommentDto.Response check_comment1 = commentService.findById(comment_id1);
        CommentDto.Response check_comment2 = commentService.findById(comment_id2);

        Assertions.assertEquals(comment1.getContent(), check_comment1.getContent());
        Assertions.assertEquals(comment2.getContent(), check_comment2.getContent());
    }

    @Test
    @DisplayName("업테이트 되는지 테스트")
    public void updateTest() {
        String password = bCryptPasswordEncoder.encode("1234");

        User user = User.builder()
                .name("박대영")
                .userId("abcd")
                .nickname("대영")
                .password(password)
                .email("abcd@qwe.zxc")
                .role(Role.USER)
                .build();
        userRepository.save(user);
        Long user_id = user.getId();

        Post post = Post.builder()
                .title("제목1")
                .content("내용1")
                .build();
        post.setUser(user);
        postRepository.save(post);
        Long post_id = post.getId();

        Comment comment = Comment.builder()
                .content("댓글1")
                .build();
        CommentDto.Request comment_dto = CommentDto.Request.builder()
                .content(comment.getContent())
                .build();
        Long comment_id1 = commentService.save(comment_dto, user_id, post_id);

        comment_dto.setContent("댓글수정");

        commentService.update(post_id, comment_id1, comment_dto);

        CommentDto.Response updatedComment = commentService.findById(comment_id1);
        Comment comment1 = Comment.builder()
                .content("댓글수정")
                .build();

        Assertions.assertEquals(comment1.getContent(), updatedComment.getContent());
    }

    @Test
    @DisplayName("전체 삭제")
    public void deleteAll() {
        userRepository.deleteAll();
    }
}
 */