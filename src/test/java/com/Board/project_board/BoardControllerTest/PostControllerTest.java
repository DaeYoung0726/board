package com.Board.project_board.BoardControllerTest;


import com.Board.project_board.entity.Role;
import com.Board.project_board.entity.User;
import com.Board.project_board.repository.UserRepository;
import com.Board.project_board.service.PostService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureDataJpa;

import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


/*@ExtendWith(SpringExtension.class)      //  @SpringBootApplication여기에 있는 빈들을 사용하기 위해
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc       // MockMvc를 Builder 없이 주입받을 수 있음.*/
//@WebMvcTest(PostController_REST.class)
@AutoConfigureDataJpa
public class PostControllerTest {
    /**
     * 웹 API 테스트할 때 사용
     * 스프링 MVC 테스트의 시작점
     * HTTP GET,POST 등에 대해 API 테스트 가능
     * */
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserRepository userRepository;
    @MockBean // PostService 빈을 Mock으로 대체
    private PostService postService;

    private User user;
    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        user = User.builder()
                .name("박대영")
                .userId("abcd")
                .nickname("대영")
                .password("QWert123@")
                .email("abcd@qwe.zxc")
                .role(Role.BRONZE)
                .build();
        userRepository.save(user);
    }
    @Test
    @DisplayName("게시글 조회 테스트")
    @WithMockUser
    public void postSearch() throws Exception {
        mockMvc.perform(get("/api/post"))
                .andExpect(status().isOk())
                .andDo(print());
    }

    // https://burpeekim.tistory.com/8
    /*@Test
    @DisplayName("게시글 작성 테스트")
    @WithMockUser
    public void postSave() throws Exception {
        given(userRepository.findByUserId(user.getUserId())).willReturn(Optional.ofNullable(user));
        System.out.println("user: " + user);
        PostDTO.Request post_dto = PostDTO.Request.builder()
                .title("제목1")
                .content("내용1")
                .build();
        String json = objectMapper.writeValueAsString(post_dto);

        this.mockMvc.perform(post("/api/post/write")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json)
                        .with(SecurityMockMvcRequestPostProcessors.user(user.getUserId()))
                        .with(csrf()))
                .andExpect(status().isOk())
                .andDo(print());
    }*/
}
