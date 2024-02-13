package com.Board.project_board.post_test;

import com.Board.project_board.dto.CommentDto;
import com.Board.project_board.dto.PostDto;
import com.Board.project_board.dto.UserDto;
import com.Board.project_board.entity.Post;
import com.Board.project_board.entity.Role;
import com.Board.project_board.entity.User;
import com.Board.project_board.repository.UserRepository;
import com.Board.project_board.service.CommentService;
import com.Board.project_board.service.PostService;
import com.Board.project_board.service.UserService;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;


import static java.lang.Thread.sleep;

/*
@SpringBootTest
public class PostApplicationTests {

    @Autowired
    private PostService postService;
    @Autowired
    private CommentService commentService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    private void checkSame(PostDto.Response post1, PostDto.Response post2) {
        Assertions.assertEquals(post1.getTitle(), post2.getTitle());
        Assertions.assertEquals(post1.getContent(), post2.getContent());
    }


    @Test
    @DisplayName("데이터가 제대로 들어갔는지 테스트")
    public void addAndgetTest() {
        String password = bCryptPasswordEncoder.encode("1234");
        User user = User.builder()
                .name("박대영6")
                .userId("abcd12")
                .nickname("대영55")
                .password(password)
                .email("abcd@qwe.zxc")
                .role(Role.USER)
                .build();
        userRepository.save(user);
        Long user_id = user.getId();

        Post post1 = Post.builder()
                .title("제목1")
                .content("내용1")
                .build();

        Post post2 = Post.builder()
                .title("제목2")
                .content("내용2")
                .build();

        PostDto.Request post_dto1 = PostDto.Request.builder()
                .title(post1.getTitle())
                .content(post1.getContent())
                .build();

        PostDto.Request post_dto2 =  PostDto.Request.builder()
                .title(post2.getTitle())
                .content(post2.getContent())
                .build();

        postService.save(post_dto1, user_id);
        postService.save(post_dto2, user_id);
        PostDto.Response check_post1 = postService.getByTitle(post_dto1.getTitle());
        PostDto.Response check_post2 = postService.getByTitle(post_dto2.getTitle());

        Assertions.assertEquals(post1.getTitle(), check_post1.getTitle());
        Assertions.assertEquals(post1.getContent(), check_post1.getContent());
        Assertions.assertEquals(post2.getTitle(), check_post2.getTitle());
        Assertions.assertEquals(post2.getContent(), check_post2.getContent());
    }

    @Test
    @DisplayName("업테이트 되는지 테스트")
    public void updateTest() throws InterruptedException {
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
        PostDto.Request post_dto = PostDto.Request.builder()
                .title(post.getTitle())
                .content(post.getContent())
                .build();

        Long post_id = postService.save(post_dto, user_id);
        sleep(1000);  // 업데이트 시간도 동시에 확인
        post_dto.setTitle("내용수정");
        post_dto.setContent("댓글수정");
        postService.update(post_id, post_dto);

        PostDto.Response updatedPost = postService.findById(post_id);
        Post post1 = Post.builder()
                .title("내용수정")
                .content("댓글수정")
                .build();

        Assertions.assertEquals(post1.getTitle(), updatedPost.getTitle());
        Assertions.assertEquals(post1.getContent(), updatedPost.getContent());
    }
    @Test
    @Transactional
    public void asdd() {
        User user = userRepository.findById(1L).orElse(null);

        if (user != null) {
            // 트랜잭션 내에서 컬렉션에 접근하여 로딩
            System.out.println("asda" + user.getPosts());
        }
    }

    @Test
    @DisplayName("양방향 관계 테스트")
    public void mappingTest() {
        String password = bCryptPasswordEncoder.encode("1234");
        UserDto.Request user = UserDto.Request.builder()
                .name("박대영d")
                .userId("abcddd")
                .nickname("대영d")
                .password(password)
                .email("abcd@qwe.zxcd")
                .role(Role.USER)
                .build();


        Long user_id = userService.create(user);

        PostDto.Request post_dto =  PostDto.Request.builder()
                .title("제목1")
                .content("내용1")
                .build();

        Long post_id = postService.save(post_dto, user_id);

        CommentDto.Request comment_dto = CommentDto.Request.builder()
                .content("댓글1")
                .build();
        Long comment_id = commentService.save(comment_dto, user_id, post_id);

        CommentDto.Response savedComment = commentService.findById(comment_id);
        Assertions.assertNotNull(savedComment);
        Assertions.assertNotNull(savedComment.getPost_id());
        Assertions.assertEquals(savedComment.getPost_id(), post_id);
    }

    @Test
    @DisplayName("게시글 삭제 시 댓글도 함께 삭제 확인")
    public void deletePostAndCommentsTest() {
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
        // 게시글 생성 및 저장

        PostDto.Request post_dto =  PostDto.Request.builder()
                .title("제목1")
                .content("내용1")
                .build();

        Long post_id = postService.save(post_dto, user_id);

        // 댓글 생성 및 저장
        CommentDto.Request comment_dto = CommentDto.Request.builder()
                .content("댓글1")
                .build();
        Long comment_id = commentService.save(comment_dto, user_id, post_id);

        // 게시글 삭제
        postService.delete(post_id);

        // 게시글 및 댓글이 삭제되었는지 확인
        Assertions.assertNull(postService.findById(post_id));
        Assertions.assertNull(commentService.findById(comment_id));
    }


    @Test
    @Transactional
    @DisplayName("User에서 comments 가져오기 테스트")
    public void asd() {
        UserDto.Response user = userService.findById(34L);
        System.out.println(user.getComments());
    }


    @Test
    @Transactional
    @DisplayName("Comment에서 user가져오기 테스트")
    public void zxc() {
        CommentDto.Response comment = commentService.findById(138L);
        System.out.println(comment.getUser_id());
    }

    @Test
    @DisplayName("전체 삭제")
    public void deleteAll() {
        userService.deleteAll();
    }


    @Test
    @DisplayName("user이 가진 게시글 가져오기")
    public void getAllUserPosts() {
        String password1 = bCryptPasswordEncoder.encode("1234");
        String password2 = bCryptPasswordEncoder.encode("1234");

        UserDto.Request userdto1 = UserDto.Request.builder()
                .name("박대영1")
                .userId("abcd")
                .nickname("대영1")
                .password(password1)
                .email("abcd@qwe.zxc")
                .role(Role.USER)
                .build();

        UserDto.Request userdto2 = UserDto.Request.builder()
                .name("박대영2")
                .userId("efgh")
                .nickname("대영2")
                .password(password2)
                .email("abcd@qwe.zxc")
                .role(Role.USER)
                .build();

        Long userid1 = userService.create(userdto1);
        Long userid2 = userService.create(userdto2);

        PostDto.Request postdto1 = PostDto.Request.builder()
                .title("제목1")
                .content("내용1")
                .build();
        PostDto.Request postdto2 = PostDto.Request.builder()
                .title("제목2")
                .content("내용2")
                .build();
        PostDto.Request postdto3 = PostDto.Request.builder()
                .title("제목3")
                .content("내용3")
                .build();
        PostDto.Request postdto4 = PostDto.Request.builder()
                .title("제목4")
                .content("내용4")
                .build();

        postService.save(postdto1, userid1);
        postService.save(postdto2, userid2);
        postService.save(postdto3, userid1);
        postService.save(postdto4, userid2);

        System.out.println("결과 : " + postService.user_postgetAll(userid1));
    }
} */
