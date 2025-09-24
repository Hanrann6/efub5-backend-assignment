package efub.assignment.community.post.service;

import efub.assignment.community.board.domain.Board;
import efub.assignment.community.board.repository.BoardRepository;
import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.repository.MemberRepository;
import efub.assignment.community.post.domain.Post;
import efub.assignment.community.post.dto.request.PostCreateRequestDto;
import efub.assignment.community.post.dto.request.UpdateContentDto;
import efub.assignment.community.post.dto.response.PostListResponseDto;
import efub.assignment.community.post.dto.response.PostResponseDto;
import efub.assignment.community.post.repository.PostRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class PostServiceTest {

    @Mock
    private PostRepository postRepository;

    @Mock
    private BoardRepository boardRepository;

    @Mock
    private MemberRepository memberRepository;

    @InjectMocks
    private PostService postService;

    private Board board;
    private Member author;
    private Post post;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        board = Board.builder()
                .owner(author)
                .name("test 게시판")
                .description("테스트 설명")
                .notice("공지")
                .build();

        ReflectionTestUtils.setField(board, "boardId", 1L);

        author = Member.builder()
                .studentId("2025")
                .university("Ewha")
                .nickname("란란란")
                .email("rann@test.com")
                .password("1234")
                .build();

        ReflectionTestUtils.setField(author, "memberId", 1L);

        post = Post.builder()
                .postId(1L)
                .content("내용임임임")
                .board(board)
                .author(author)
                .build();
    }

    // 게시글 생성 성공
    @Test
    void 게시글_생성_성공() {
        // given
        PostCreateRequestDto requestDto = new PostCreateRequestDto(1L, false, author.getMemberId(), "내용임임임");

        // Board, Member mock 동작
        given(boardRepository.findByBoardId(board.getBoardId())).willReturn(Optional.of(board));
        given(memberRepository.findByMemberId(author.getMemberId())).willReturn(Optional.of(author));
        given(postRepository.save(any(Post.class))).willReturn(post);

        // when
        PostResponseDto response = postService.createPost(requestDto);

        // then
        assertThat(response.getContent()).isEqualTo("내용임임임");
        assertThat(response.getAuthorId()).isEqualTo(author.getMemberId());
        assertThat(response.getBoardId()).isEqualTo(board.getBoardId());
    }

    // 게시글 생성 실패 - Board 없음
    @Test
    void 게시글생성실패_Board없음() {
        // given
        PostCreateRequestDto requestDto =
                new PostCreateRequestDto(1L, false, 1L, "내용임임임");
        given(boardRepository.findByBoardId(99L)).willReturn(Optional.empty());

        // when & then
        assertThrows(RuntimeException.class, () -> postService.createPost(requestDto));
    }

    // 게시글 수정
    @Test
    void 게시글_수정_성공() {
        // given
        UpdateContentDto updateDto = new UpdateContentDto();
        updateDto.setContent("바꾼 내용입니다!");

        given(postRepository.findByPostId(post.getPostId())).willReturn(Optional.of(post));
        given(postRepository.save(post)).willReturn(post);

        // when
        PostResponseDto response = postService.updateContent(post.getPostId(), updateDto);

        // then
        assertThat(response.getContent()).isEqualTo("바꾼 내용입니다!");
        verify(postRepository, times(1)).save(post);
    }

    // 게시글 조회
    @Test
    void 게시글_조회() {
        // given
        given(postRepository.findByPostId(post.getPostId())).willReturn(Optional.of(post));

        // when
        PostResponseDto response = postService.getPost(post.getPostId());

        // then
        assertThat(response.getContent()).isEqualTo("내용임임임");
        assertThat(response.getAuthorId()).isEqualTo(author.getMemberId());

    }

    // 게시글 목록 조회
    @Test
    void 게시글_목록조회() {
        // given
        given(boardRepository.findByBoardId(board.getBoardId())).willReturn(Optional.of(board));
        given(postRepository.findAllByBoard(board)).willReturn(List.of(post));

        // when
        PostListResponseDto response = postService.getPostList(board.getBoardId());

        // then
        assertThat(response.posts()).hasSize(1);
        assertThat(response.posts().get(0).getContent()).isEqualTo("내용임임임");
        assertThat(response.posts().get(0).getAuthorId()).isEqualTo(author.getMemberId());
    }

    // 게시글 삭제
    @Test
    void 게시글_삭제() {
        // when
        postService.deletePost(post.getPostId());

        // then
        verify(postRepository, times(1)).deleteByPostId(post.getPostId());
    }
}