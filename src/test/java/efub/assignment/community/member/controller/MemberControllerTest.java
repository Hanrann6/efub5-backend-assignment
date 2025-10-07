package efub.assignment.community.member.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import efub.assignment.community.comment.dto.response.MemberCommentResponseDto;
import efub.assignment.community.comment.service.CommentService;
import efub.assignment.community.member.dto.request.MemberRequestDto;
import efub.assignment.community.member.dto.response.MemberResponseDto;
import efub.assignment.community.member.service.MemberService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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

import static org.junit.jupiter.api.Assertions.*;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(MemberController.class)
@MockBean(JpaMetamodelMappingContext.class)
class MemberControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private MemberService memberService;

    @MockBean
    private CommentService commentService;

    private MemberResponseDto memberResponseDto;

    @BeforeEach
    void setUp() {
        memberResponseDto = MemberResponseDto.builder()
                .studentId("2025")
                .university("Ewha")
                .nickname("란란란")
                .email("rann@test.com")
                .build();
    }


    // 회원 생성
    @Test
    @DisplayName("회원 생성 - 실패 케이스")
    void createMember() throws Exception {
        MemberRequestDto requestDto = MemberRequestDto.builder()
                .studentId("2025")
                .university("Ewha")
                .nickname("란란란")
                .email("rann@test.com")
                .password("Aa123456789000!@#") // 비밀번호 형식에 원래 #이 들어가면 안 됨
                .build();

        given(memberService.createMember(any(MemberRequestDto.class)))
                .willReturn(memberResponseDto);

        mockMvc.perform(post("/members")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nickname").value("란란란"))
                .andExpect(jsonPath("$.email").value("rann@test.com"));
    }

    // 회원 조회
    @Test
    @DisplayName("회원 조회 - 성공")
    void getMember() throws Exception {
        given(memberService.getMember(1L)).willReturn(memberResponseDto);

        mockMvc.perform(get("/members/{memberId}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("란란란"))
                .andExpect(jsonPath("$.email").value("rann@test.com"));
    }

    // 닉네임 수정
    @Test
    @DisplayName("회원 닉네임 수정 - 성공")
    void updateMember() throws Exception {
        MemberRequestDto updateDto = new MemberRequestDto(
                "2025", "Ewha", "new란", "rann@test.com", "Aa123456789000!@"
        );

        MemberResponseDto updatedResponse = MemberResponseDto.builder()
                .studentId("2025")
                .university("Ewha")
                .nickname("new란")
                .email("rann@test.com")
                .build();


        given(memberService.updateMember(eq(1L), any(MemberRequestDto.class)))
                .willReturn(updatedResponse);

        mockMvc.perform(patch("/members/profile/{memberId}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value("new란"));
    }

    // 회원 탈퇴
    @Test
    @DisplayName("회원 탈퇴 - 성공")
    void deleteMember() throws Exception {
        BDDMockito.willDoNothing().given(memberService).deleteMember(1L);

        mockMvc.perform(patch("/members/{memberId}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("성공적으로 탈퇴가 완료되었습니다."));
    }

    // 작성자별 댓글 조회
    @Test
    @DisplayName("작성자별 댓글 조회 - 성공")
    void getMemberComments() throws Exception {
        MemberCommentResponseDto commentResponse = MemberCommentResponseDto.builder()
                .memberId(1L)
                .commentList(List.of())
                .count(0L)
                .build();

        given(commentService.getMemberCommentList(1L)).willReturn(commentResponse);

        mockMvc.perform(get("/members/{memberId}/comments", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.commentList").isArray())
                .andExpect(jsonPath("$.count").value(0));
    }
}