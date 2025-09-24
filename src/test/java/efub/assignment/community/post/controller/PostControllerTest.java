package efub.assignment.community.post.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import efub.assignment.community.post.dto.request.PostCreateRequestDto;
import efub.assignment.community.post.dto.request.UpdateContentDto;
import efub.assignment.community.post.dto.response.PostListResponseDto;
import efub.assignment.community.post.dto.response.PostResponseDto;
import efub.assignment.community.post.service.PostService;
import efub.assignment.community.comment.service.CommentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.BDDMockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.jpa.mapping.JpaMetamodelMappingContext;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PostController.class)
@MockBean(JpaMetamodelMappingContext.class)
class PostControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private PostService postService;

    @MockBean
    private CommentService commentService;

    private PostResponseDto postResponseDto;

    @BeforeEach
    void setUp() {
        postResponseDto = PostResponseDto.builder()
                .postId(1L)
                .boardId(1L)
                .authorId(1L)
                .anonymous(false)
                .content("내용입니다")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // 게시글 생성
    @Test
    void 게시글_생성() throws Exception {
        PostCreateRequestDto requestDto = new PostCreateRequestDto(1L, false, 1L, "내용입니다");

        given(postService.createPost(any(PostCreateRequestDto.class))).willReturn(postResponseDto);

        mockMvc.perform(post("/posts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.content").value("내용입니다"))
                .andExpect(jsonPath("$.authorId").value(1L));
    }

    // 게시글 수정
    @Test
    void 게시글_수정() throws Exception {
        UpdateContentDto updateDto = new UpdateContentDto();
        // reflection으로 필드 주입 or setter 있으면 사용
        // 여기서는 ObjectMapper로 직렬화만 하면 충분
        updateDto.setContent("수정된 내용");

        PostResponseDto updated = PostResponseDto.builder()
                .postId(1L)
                .boardId(1L)
                .authorId(1L)
                .anonymous(false)
                .content("수정된 내용")
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        given(postService.updateContent(eq(1L), any(UpdateContentDto.class))).willReturn(updated);

        mockMvc.perform(patch("/posts/{postId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value("수정된 내용"));
    }

    // 게시글 단건 조회
    @Test
    void 게시글_조회() throws Exception {
        given(postService.getPost(1L)).willReturn(postResponseDto);

        mockMvc.perform(get("/posts/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.postId").value(1L))
                .andExpect(jsonPath("$.content").value("내용입니다"));
    }

    // 게시글 목록 조회
    @Test
    void 게시글_목록조회() throws Exception {
        PostListResponseDto listResponse =
                new PostListResponseDto(1L, 1, List.of(postResponseDto));
        given(postService.getPostList(1L)).willReturn(listResponse);

        mockMvc.perform(get("/posts/{boardId}/list", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.count").value(1));
    }

    // 게시글 삭제
    @Test
    void 게시글_삭제() throws Exception {
        BDDMockito.willDoNothing().given(postService).deletePost(1L);

        mockMvc.perform(delete("/posts/{postId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("성공적으로 게시글 삭제가 완료되었습니다."));
    }
}