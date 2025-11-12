package efub.assignment.community.member.service;

import efub.assignment.community.member.domain.Member;
import efub.assignment.community.member.domain.MemberStatus;
import efub.assignment.community.member.dto.request.MemberRequestDto;
import efub.assignment.community.member.dto.response.MemberResponseDto;
import efub.assignment.community.member.repository.MemberRepository;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MemberService {

    private final MemberRepository memberRepository;

    @Transactional
    // 회원 생성
    public MemberResponseDto createMember(MemberRequestDto memberRequestDto) {
        // 이메일 중복 검사
        if(memberRepository.existsByEmail(memberRequestDto.getEmail())) {
            throw new IllegalArgumentException("Email already exists");
        }
        // 학번 중복 검사
        if(memberRepository.existsByStudentId(memberRequestDto.getStudentId())) {
            throw new IllegalArgumentException("StudentId already exists");
        }
        Member member = memberRequestDto.toEntity();
        Member savedMember = memberRepository.save(member);
        return MemberResponseDto.from(savedMember);
    }

    // 회원 조회
    @Transactional(readOnly = true)
    public MemberResponseDto getMember(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));
        return MemberResponseDto.from(member);
    }

    @Transactional
    // 닉네임 수정
    public MemberResponseDto updateMember(Long memberId, MemberRequestDto memberRequestDto) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        member.updateNickname(memberRequestDto.getNickname());
        Member savedMember = memberRepository.save(member);
        return MemberResponseDto.from(savedMember);
    }

    // 닉네임으로 회원 조회
    @Transactional(readOnly = true)
    public List<MemberResponseDto> getMembersByNickname(String nickname) {
        List<Member> members = memberRepository.findMembersByNickname(nickname);

        return members.stream()
                .map(MemberResponseDto::from)
                .collect(Collectors.toList());
    }

    @Transactional
    // 회원 탈퇴 (상태 변경)
    public void deleteMember(Long memberId) {
        Member member = memberRepository.findByMemberId(memberId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        member.changeStatus(MemberStatus.DEACTIVATED);
        memberRepository.save(member);
    }
}
