package com.Board.project_board.service;

import com.Board.project_board.dto.PostDto;
import com.Board.project_board.entity.Category;
import com.Board.project_board.entity.Post;
import com.Board.project_board.entity.User;
import com.Board.project_board.repository.CategoryRepository;
import com.Board.project_board.repository.PostRepository;
import com.Board.project_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    /* 게시글 생성. */
    @Transactional
    public Long save(PostDto.Request dto, String category_name, String username) {

        log.info("Saving post");
        User user = userRepository.findByUserId(username).orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다. username: " + username));
        Category category = categoryRepository.findByName(category_name).orElseThrow(() ->
                new IllegalArgumentException("해당 카테고리가 존재하지 않습니다. name: " + category_name));
        dto.setUser(user);
        dto.setCategory(category);
        Post post = dto.toEntity();

        postRepository.save(post);
        log.info("Post saved with ID: {}", post.getId());
        return post.getId();
    }

    /* 게시글 읽기 */
    @Transactional(readOnly = true)   // (readOnly = true) 뒤에 붙이면 조회속도 향상. 읽기만 하기에
    public PostDto.Response findById(Long id) {

        log.info("Finding post by ID: {}", id);
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id: " + id));
        return new PostDto.Response(post);
    }

    /* 제목으로 게시글 읽기 */
    @Transactional(readOnly = true)
    public PostDto.Response getByTitle(String title) {

        log.info("Finding post by title: {}", title);
        return new PostDto.Response(postRepository.findByTitle(title));
    }

    /* 게시글 전체 읽기. 페이징 x. */
    @Transactional(readOnly = true)
    public List<PostDto.Response> findAll() {

        log.info("Finding all posts");
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(PostDto.Response::new).collect(Collectors.toList());
        // return posts.stream().map(post -> new PostDTO.Response(post)).collect(Collectors.toList());
    }

    /* 사용자가 쓴 게시글. */
    @Transactional(readOnly = true)
    public Page<PostDto.Response> user_postFindAll(Long user_id, Pageable pageable) {

        log.info("Finding posts by user ID: {}", user_id);
        Page<Post> posts = postRepository.findByUserId(user_id, pageable);
        return posts.map(PostDto.Response::new);
    }

    /* 게시글 삭제. */
    @Transactional
    public void delete(Long id, String username) {               // delete

        log.info("Deleting post with ID: {}", id);
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id: " + id));
        if(!post.getUser().getUserId().equals(username))
            throw new RuntimeException("권한이 없습니다.");
        else {
            postRepository.delete(post);
            log.info("Post deleted successfully");
        }
    }

    /* 게시글 업데이트. */
    @Transactional
    public void update(Long id, String username, PostDto.UpdateRequest dto) {  // update

        log.info("Updating post with ID: {}", id);
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id: " + id));
        if(!post.getUser().getUserId().equals(username))
            throw new RuntimeException("권한이 없습니다.");
        else {
            post.update(dto.getTitle(), dto.getContent());
            log.info("Post updated successfully");
        }
    }

    /* 게시글 뷰 업데이트 */
    @Transactional
    public void updateView(Long id) {

        log.info("Updating view count for post with ID: {}", id);
        postRepository.updateView(id);
        log.info("View count updated successfully");
    }

    /* 게시글 읽기. 페이징 처리. */
    @Transactional(readOnly = true)
    public Page<PostDto.Response> postList(Pageable pageable) {

        log.info("Fetching list of posts with pagination");
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(PostDto.Response::new);
    }

    /* 게시글 검색. 페이징 처리. */
    @Transactional(readOnly = true)
    public Page<PostDto.Response> postSearchList(String searchKeyword, Pageable pageable) {

        log.info("Searching posts by keyword: {}", searchKeyword);
        Page<Post> posts = postRepository.findByTitleContaining(searchKeyword, pageable);
        return posts.map(PostDto.Response::new);
    }

    /* 카테고리에 담긴 게시글 가지고 오기. 페이징 처리. */
    @Transactional(readOnly = true)
    public Page<PostDto.Response> findByCategoryName(String category_name, Pageable pageable) {

        log.info("Finding posts by category name: {}", category_name);
        Page<Post> posts = postRepository.findByCategoryName(category_name, pageable);
        return posts.map(PostDto.Response::new);
    }

    /* 카테고리 속 게시글 검색. 페이징 처리. */
    @Transactional(readOnly = true)
    public Page<PostDto.Response> findByCategoryNameAndTitleContaining(String category_name,
                                                                       String searchKeyword, Pageable pageable) {

        log.info("Finding posts by category name: {} and title containing: {}", category_name, searchKeyword);
        Page<Post> posts = postRepository.findByCategoryNameAndTitleContaining(category_name, searchKeyword, pageable);

        return posts.map(PostDto.Response::new);
    }

    /* 게시글 좋아요 업데이트. */
    @Transactional
    public void updateLikeCount(Long id, int value) {
        log.info("Updating like count for post with ID: {}", id);
        postRepository.updateLikeCount(id, value);
        log.info("Like count updated successfully");
    }

    /* 게시글 신고기능. */
    @Transactional
    public int updateReport(Long id) {

        log.info("Updating report count for post with ID: {}", id);
        postRepository.updateReport(id);

        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id: " + id));

        if(post.getReport() == 5) {     // updateReport가 post 위에 있기에  post.getReport() == 5. 아래에 있으면 4
            log.info("Deleting post with ID: {}", id);
            postRepository.delete(post);
            log.info("Report count updated && Post deleted successfully");
            return 1;
        }

        log.info("Report count updated successfully");
        return 0;
    }

}
