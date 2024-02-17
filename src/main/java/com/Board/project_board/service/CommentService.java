package com.Board.project_board.service;

import com.Board.project_board.dto.CommentDto;
import com.Board.project_board.entity.Comment;
import com.Board.project_board.entity.Post;
import com.Board.project_board.entity.User;
import com.Board.project_board.repository.CommentRepository;
import com.Board.project_board.repository.PostRepository;
import com.Board.project_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommentService {

    private final CommentRepository commentRepository;
    private final PostRepository postRepository;      // 양방향 매핑을 위해.
    private final UserRepository userRepository;

    /* 댓글 생성. */
    @Transactional
    public Long save(CommentDto.Request dto, Long user_Id, Long post_Id) {

        log.info("Creating a new comment for post with ID: {} by user with ID: {}", post_Id, user_Id);
        Post post = postRepository.findById(post_Id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id: " + post_Id));;
        User user = userRepository.findById(user_Id).orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다. id: " + user_Id));;

        dto.setPost(post);
        dto.setUser(user);

        Comment comment = dto.toEntity();

        commentRepository.save(comment);
        log.info("Comment created with ID: {}", comment.getId());

        return comment.getId();
    }

    /* 댓글 찾기. */
    @Transactional(readOnly = true)
    public CommentDto.Response findById(Long id) {

        log.info("Finding comment by ID: {}", id);
        Comment comment = commentRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 댓글이 존재하지 않습니다. id: " + id));
        return new CommentDto.Response(comment);
    }

    /* 전체 댓글 찾기. */
    @Transactional(readOnly = true)
    public List<CommentDto.Response> findAll(Long post_Id) {

        log.info("Finding all comments for post with ID: {}", post_Id);
        Post post = postRepository.findById(post_Id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id: " + post_Id));
        List<Comment> comments = post.getComments();
        return comments.stream().map(CommentDto.Response::new).collect(Collectors.toList());
    }

    /* 댓글 삭제. */
    @Transactional
    public void delete(Long post_id, Long id) {

        log.info("Deleting comment with ID: {}", id);
        Comment comment = commentRepository.findByPostIdAndId(post_id, id).orElseThrow(() ->    // 왜 이걸로 할까 안해도 될듯? 짜핀 순차적으로 id가 지정됨.
                                                                                                // 그리고 다른 사용자가 문제라면 권한 설정화면 됨.
                new IllegalArgumentException("해당 댓글이 존재하지 않습니다. id: " + id));
        commentRepository.delete(comment);
        log.info("Comment deleted successfully");
    }

    /* 전체 댓글 삭제. */
    @Transactional
    public void deleteAll() {

        log.info("Deleting all comments");
        commentRepository.deleteAll();
        log.info("All comments deleted successfully");
    }

    /* 댓글 업데이트. */
    @Transactional
    public void update(Long post_id, Long id, CommentDto.Request dto) {  // update

        log.info("Updating comment with ID: {}", id);
        Comment comment = commentRepository.findByPostIdAndId(post_id, id).orElseThrow(() ->
                new IllegalArgumentException("해당 댓글이 존재하지 않습니다. id: " + id));
        comment.update(dto.getContent());
        log.info("Comment updated successfully");
    }
}
