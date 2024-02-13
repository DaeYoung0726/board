package com.Board.project_board.service;

import com.Board.project_board.dto.PostDto;
import com.Board.project_board.entity.Category;
import com.Board.project_board.entity.Post;
import com.Board.project_board.entity.User;
import com.Board.project_board.repository.CategoryRepository;
import com.Board.project_board.repository.PostRepository;
import com.Board.project_board.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostService {
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;

    /* 게시글 생성. */
    @Transactional
    public Long save(PostDto.Request dto, String category_name, Long user_id) {
        User user = userRepository.findById(user_id).orElseThrow(() ->
                new IllegalArgumentException("해당 회원이 존재하지 않습니다. id: " + user_id));
        Category category = categoryRepository.findByName(category_name).orElseThrow(() ->
                new IllegalArgumentException("해당 카테고리가 존재하지 않습니다. name: " + category_name));
        dto.setUser(user);
        dto.setCategory(category);
        Post post = dto.toEntity();
        //user.getPosts().add(post);   // getById를 사용하기에 이걸 사용 안하면 쿼리 적게 날림.

        postRepository.save(post);
        return post.getId();
    }

    /* 게시글 읽기 */
    @Transactional(readOnly = true)   // (readOnly = true) 뒤에 붙이면 조회속도 향상. 읽기만 하기에
    public PostDto.Response findById(Long id) {
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id: " + id));
        return new PostDto.Response(post);
    }

    /* 제목으로 게시글 읽기 */
    @Transactional(readOnly = true)
    public PostDto.Response getByTitle(String title) {
        return new PostDto.Response(postRepository.findByTitle(title));
    }

    /* 게시글 전체 읽기. 페이징 x. */
    @Transactional(readOnly = true)
    public List<PostDto.Response> findAll() {
        List<Post> posts = postRepository.findAll();
        return posts.stream().map(PostDto.Response::new).collect(Collectors.toList());
        // return posts.stream().map(post -> new PostDTO.Response(post)).collect(Collectors.toList());
    }

    /* 사용자가 쓴 게시글. */
    @Transactional(readOnly = true)
    public Page<PostDto.Response> user_postFindAll(Long user_id, Pageable pageable) {
        Page<Post> posts = postRepository.findByUserId(user_id, pageable);
        return posts.map(PostDto.Response::new);
    }

    /* 게시글 삭제. */
    @Transactional
    public void delete(Long id) {               // delete
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id: " + id));
        postRepository.delete(post);
    }

    /* 게시글 업데이트. */
    @Transactional
    public void update(Long id, PostDto.Request dto) {  // update
        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id: " + id));
        post.update(dto.getTitle(), dto.getContent());
    }

    /* 게시글 뷰 업데이트 */
    @Transactional
    public void updateView(Long id) {
        postRepository.updateView(id);
    }

    /* 게시글 읽기. 페이징 처리. */
    @Transactional(readOnly = true)
    public Page<PostDto.Response> postList(Pageable pageable) {
        Page<Post> posts = postRepository.findAll(pageable);
        return posts.map(PostDto.Response::new);
    }

    /* 게시글 검색. 페이징 처리. */
    @Transactional(readOnly = true)
    public Page<PostDto.Response> postSearchList(String searchKeyword, Pageable pageable) {
        Page<Post> posts = postRepository.findByTitleContaining(searchKeyword, pageable);
        return posts.map(PostDto.Response::new);
    }

    /* 카테고리에 담긴 게시글 가지고 오기. 페이징 처리. */
    @Transactional(readOnly = true)
    public Page<PostDto.Response> findByCategoryName(String category_name, Pageable pageable) {
        Page<Post> posts = postRepository.findByCategoryName(category_name, pageable);
        return posts.map(PostDto.Response::new);
    }

    /* 카테고리 속 게시글 검색. 페이징 처리. */
    @Transactional(readOnly = true)
    public Page<PostDto.Response> findByCategoryNameAndTitleContaining(String category_name,
                                                                       String searchKeyword, Pageable pageable) {
        Page<Post> posts = postRepository.findByCategoryNameAndTitleContaining(category_name, searchKeyword, pageable);

        return posts.map(PostDto.Response::new);
    }

    /* 게시글 좋아요 업데이트. */
    @Transactional
    public void updateLikeCount(Long id, int value) {
        postRepository.updateLikeCount(id, value);
    }

    /* 게시글 신고기능. */
    @Transactional
    public int updateReport(Long id) {
        postRepository.updateReport(id);

        Post post = postRepository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("해당 게시글이 존재하지 않습니다. id: " + id));

        if(post.getReport() == 5) {     // updateReport가 post 위에 있기에  post.getReport() == 5. 아래에 있으면 4
            postRepository.delete(post);
            return 1;
        }
        return 0;
    }

}
